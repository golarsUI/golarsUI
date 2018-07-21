import { Component, OnInit, ViewChild, Input } from '@angular/core';
import {RequestOptions, Request, RequestMethod} from '@angular/http';
import { Observable } from 'rxjs';
import { Headers } from '@angular/http'
declare var $: any;
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { ImportFieldValues } from './import.mapping';
import { FileUpload } from 'primeng/primeng';
import { ImportService } from '../services/import.service';
import { CommonService } from '../services/common.service';
@Component({
  selector: 'golars-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.css']
})
export class ImportComponent implements OnInit {
  importForm;
  @ViewChild('fileInput') fileInput: FileUpload;
  @Input() selectedFolder;
  docTypes=[];
  docTypesFullList = ImportFieldValues.docTypeMapping;
  stateProgram= ImportFieldValues.stateProgramMapping;
  scopeOfWork = [];
  scopeOfWorkFullList = ImportFieldValues.scopeOfWorkMapping;
  stateProgramMappingForDocumentType = ImportFieldValues.stateProgramMappingForDocumentType;
  stateProgramMappingForScopeOfWork = ImportFieldValues.stateProgramMappingForScopeOfWork;
  editDocument = false;
  importDocName;
  docData ;
  showSuccessMessage=false;
  successMessage=null;
  showFileSelectErrorMessage=false;
  fileSelectErrorMessage=null
  importFolder;
defaultdate;
  model: any = {};
  constructor(private http: HttpClient,private importService: ImportService,private commonService: CommonService) { }

  ngOnInit() {

    

    var self = this;
    $('body').on('hide.bs.modal', '.modal', function ($event) {
      if ($event.target.id != "importModal") return;

      self.commonService.removeEditUser();
      self.showSuccessMessage = false;
      self.successMessage = null;
      self.showFileSelectErrorMessage = false;
      self.fileSelectErrorMessage = null;
      if(self.fileInput!=null)
      self.fileInput.files = [];
      self.model = {}
      self.model.docUpdateDate = new Date();
      self.editDocument = false;
      

    })
  $('body').on('show.bs.modal', '.modal', function($event){
    if($event.target.id!="importModal") return;
    if (self.commonService.getDocData() != null) {
        
      self.editDocument = true;
      var docData = JSON.parse(localStorage.getItem("docData"));
      self.docData  = docData;
      self.fillImportDialogData(docData)
    }
    if(self.commonService.getStateProgramPreferences()!=null){
      self.stateProgram=[];
    var stateProgramValues = self.commonService.getStateProgramPreferences();
    self.stateProgramMappingForDocumentType = self.commonService.getDocumentTypePreferences();
    self.stateProgramMappingForScopeOfWork = self.commonService.getScopeOfWorkPreferences();
    for(var i=0;i<stateProgramValues.length;i++){
      if(stateProgramValues[i].enable)
      self.stateProgram.push(stateProgramValues[i]);
    }
  }

})

  this.commonService.notifyObservable$.subscribe((treeNode) => {
    if(treeNode !== null && treeNode !== undefined && treeNode.type === "fetchSubFolders"){
    if(treeNode.node.label !== null){
    this.selectedFolder = treeNode.node;
    this.importFolder = treeNode.node.label;

    }
}
});
// set default value for Date Document updated
this.model.docUpdateDate = new Date();
  }
  fillImportDialogData(docData) {
    this.importDocName = docData.label;
    // this.fileInput.files[0] = new File([""], docData.label);
    if (docData.properties.docUpdateDate != null)
      this.model.docUpdateDate = new Date(docData.properties.docUpdateDate);
    this.model.fecilityName = docData.properties.fecilityName;
    if (docData.properties.docDate != null)
      this.model.docDate = new Date(docData.properties.docDate);
    this.model.fid = docData.properties.fid;
    this.model.city = docData.properties.city;
    this.model.street = docData.properties.street;
    this.model.stateProgram = docData.properties.stateProgram;
    this.getStateProgramAndScopeOfWorkDropDOwn(null)
    this.model.active = docData.properties.active;
    this.model.docTypes = docData.properties.docTypes;
    this.model.scopeOfWork = docData.properties.scopeOfWork;

  }
  updateDocumentProperties(){
    // console.log(this.docData)
    var docProperties = this.getDocumentProperties();
    this.docData.properties = JSON.parse(docProperties);
    this.importService.updateDocumentPropeties(this.docData.parentid,this.docData.label,docProperties) .subscribe(
      message => {
        // console.log(message)
        if (message == true) {
          this.showSuccessMessage = true;
          this.successMessage = "Document Properties Updated Successfully !!";
          this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedFolder, isDocumentsRequired: true });
          if (this.commonService.getDocData() != null) {
            this.commonService.notify({ type: 'documentDetails', node: this.docData, isDocumentsRequired: true });
            }
        }       },
      error => {

        console.log(error);
      });
  }
  importDocuments(){
// console.log(this.fileInput.files.length)
if(this.fileInput.files.length == 0){
  this.showFileSelectErrorMessage=true;
  this.fileSelectErrorMessage="Please select at least one file to Import"
  return;
}
const frmData = new FormData();

    
    for (var i = 0; i < this.fileInput.files.length; i++) { 
      // console.log(this.fileInput);
      frmData.append("fileUpload", this.fileInput.files[i]);
    }
    frmData.append("docProperties",this.getDocumentProperties());
    frmData.append("folderProperties",this.getFolderDetails());
    // frmData.set("documentProperties",this.getDocumentProperties())
    this.importService.importDocuments(frmData)
    .subscribe(
        message => {
          // console.log(message)
          if(message == true){
          this.showSuccessMessage=true;
          this.successMessage = "File(s) Imported Successfully !!";
          this.fileInput.files=[];
          this.model={}
          this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedFolder, isDocumentsRequired: true });
        }else
        {
          this.showFileSelectErrorMessage=true;
          this.fileSelectErrorMessage="Document(s) already exists"
        }
          
        },
        error => {
          
            console.log(error);
        });
  }
  onBasicUpload($event){
    this.showFileSelectErrorMessage=false;
  this.fileSelectErrorMessage=null;
  this.showSuccessMessage=false;
  this.successMessage = null;
  }
  setModalValue(activeCheckbox){
    if(activeCheckbox.checked)
    this.model.active.value='active';
    else
    this.model.active.value=''
  }
  getDocumentProperties(){
    // console.log(this.model)
    return  JSON.stringify(this.model)
  }
  tmpFolder:any
  getFolderDetails(){
    this.tmpFolder = new Object()
    this.tmpFolder.username = this.selectedFolder.username;
    this. tmpFolder.parentid = this.selectedFolder.parentid;
    this.tmpFolder.label = this.selectedFolder.label;
    this.tmpFolder.id = this.selectedFolder.id;
    this.tmpFolder.folder = this.selectedFolder.folder;
    this.tmpFolder.expandedIcon = this.selectedFolder.expandedIcon;
    this.tmpFolder.collapsedIcon = this.selectedFolder.collapsedIcon;
    this.tmpFolder.expanded = this.selectedFolder.expanded;
    //  console.log( this.tmpFolder)
    return  JSON.stringify( this.tmpFolder)
  }
  getStateProgramAndScopeOfWorkDropDOwn($event){
    var selectedValue =null;
    if($event != null ) 
    selectedValue = $event.value;
    else 
    selectedValue =   this.model.stateProgram;
    // console.log(selectedValue)
    this.docTypes=[];
    this.scopeOfWork=[];
    this.model.scopeOfWork=[];
    for(var i=0;i<this.stateProgramMappingForDocumentType.length;i++){
      if(this.stateProgramMappingForDocumentType[i].label === selectedValue)
      this.constructDocumentType (this.stateProgramMappingForDocumentType[i].properties)
     
    // if(this.docTypesFullList[i].key != null && this.docTypesFullList[i].key.indexOf(selectedValue)>=0)
    // this.docTypes.push(this.docTypesFullList[i])
  }
  for(var i=0;i<this.stateProgramMappingForScopeOfWork.length;i++){
    if(this.stateProgramMappingForScopeOfWork[i].label === selectedValue)
    this.constructScopeOfObject (this.stateProgramMappingForScopeOfWork[i].properties)
  } 
  // for(var i=0;i<this.scopeOfWorkFullList.length;i++){
  //   if(this.scopeOfWorkFullList[i].key != null && this.scopeOfWorkFullList[i].key.indexOf(selectedValue)>=0)
  //   this.scopeOfWork.push(this.scopeOfWorkFullList[i])
  // } 
  }
  constructDocumentType(docTypeObj){
    for(var i=0;i<docTypeObj.length;i++)
    if(docTypeObj[i].enable != null && docTypeObj[i].enable == true)
    this.docTypes.push(docTypeObj[i])
  }
  constructScopeOfObject(docTypeObj){
    for(var i=0;i<docTypeObj.length;i++)
    if(docTypeObj[i].enable != null && docTypeObj[i].enable == true)
    this.scopeOfWork.push(docTypeObj[i])
  }

}
