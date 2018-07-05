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
  docTypes=ImportFieldValues.docTypeMapping;
  stateProgram= ImportFieldValues.stateProgramMapping;
  scopeOfWork = ImportFieldValues.scopeOfWorkMapping;
  showSuccessMessage=false;
  successMessage=null;
  showFileSelectErrorMessage=false;
  fileSelectErrorMessage=null

  model: any = {};
  constructor(private http: HttpClient,private importService: ImportService,private commonService: CommonService) { }

  ngOnInit() {
    var self = this;
    $('body').on('hide.bs.modal', '.modal', function($event){
      if($event.target.id!="importModal") return;
      self.showSuccessMessage=false;
      self.successMessage = null;
      self.fileInput.files=[];
      self.model={}

  })
  this.commonService.notifyObservable$.subscribe((treeNode) => {
    if(treeNode !== null && treeNode !== undefined && treeNode.type === "fetchSubFolders"){
    if(treeNode.node.label !== null){
    this.selectedFolder = treeNode.node;

    }
}
});
  }
  importDocuments(){
console.log(this.fileInput.files.length)
if(this.fileInput.files.length == 0){
  this.showFileSelectErrorMessage=true;
  this.fileSelectErrorMessage="Please select at least one file to upload"
  return;
}
const frmData = new FormData();

    
    for (var i = 0; i < this.fileInput.files.length; i++) { 
      frmData.append("fileUpload", this.fileInput.files[i]);
    }
    frmData.append("docProperties",this.getDocumentProperties());
    frmData.append("folderProperties",this.getFolderDetails());
    // frmData.set("documentProperties",this.getDocumentProperties())
    this.importService.importDocuments(frmData)
    .subscribe(
        message => {
          // console.log(message)
          this.showSuccessMessage=true;
          this.successMessage = "File Upload Successfully !!";
          this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedFolder, isDocumentsRequired: true });
          
        },
        error => {
          
            console.log(error);
        });
  }
  onBasicUpload($event){
    this.showFileSelectErrorMessage=false;
  this.fileSelectErrorMessage=null
  }
  setModalValue(activeCheckbox){
    if(activeCheckbox.checked)
    this.model.active.value='active';
    else
    this.model.active.value=''
  }
  getDocumentProperties(){
    console.log(this.model)
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
     console.log( this.tmpFolder)
    return  JSON.stringify( this.tmpFolder)
  }
}
