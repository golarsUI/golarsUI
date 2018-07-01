import { Component, OnInit, ViewChild } from '@angular/core';
import {RequestOptions, Request, RequestMethod} from '@angular/http';
import { Observable } from 'rxjs';
import { Headers } from '@angular/http'
declare var $: any;
import { environment } from '../../environments/environment';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { ImportFieldValues } from './import.mapping';
import { FileUpload } from 'primeng/primeng';
import { ImportService } from '../services/import.service';
@Component({
  selector: 'golars-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.css']
})
export class ImportComponent implements OnInit {
  importForm;
  @ViewChild('fileInput') fileInput: FileUpload;
  docTypes=ImportFieldValues.docTypeMapping;
  stateProgram= ImportFieldValues.stateProgramMapping;
  scopeOfWork = ImportFieldValues.scopeOfWorkMapping;
  showSuccessMessage=false;
  successMessage=null;

  model: any = {};
  constructor(private http: HttpClient,private importService: ImportService) { }

  ngOnInit() {
    var self = this;
    $('body').on('hide.bs.modal', '.modal', function($event){
      if($event.target.id!="importModal") return;
      self.showSuccessMessage=false;
      self.successMessage = null;
      self.fileInput.files=[];

  })
  }
  importDocuments(){
console.log(this.fileInput.files.length)

const frmData = new FormData();
    
    for (var i = 0; i < this.fileInput.files.length; i++) { 
      frmData.append("fileUpload", this.fileInput.files[i]);
    }
    frmData.append("docProperties",this.getDocumentProperties());
    // frmData.set("documentProperties",this.getDocumentProperties())
    this.importService.importDocuments(frmData)
    .subscribe(
        message => {
          // console.log(message)
          this.showSuccessMessage=true;
          this.successMessage = "File Upload Successfully !!";
          
        },
        error => {
          
            console.log(error);
        });

    // this.http.post('http://localhost:8080/golars/rest/import/upload', frmData).subscribe(
    //   data => {
    //     // SHOW A MESSAGE RECEIVED FROM THE WEB API.
    //     console.log( data as string);
    //     // console.log (this.sMsg);
    //   },
    //   (err: HttpErrorResponse) => {
    //     console.log (err.message);    // SHOW ERRORS IF ANY.
    //   }
    // );

  }
  myUploader($event){
    console.log($event)
  }
  setModalValue(activeCheckbox){
    if(activeCheckbox.checked)
    this.model.active.value='active';
    else
    this.model.active.value=''
    console.log();
  }
  getDocumentProperties(){
    console.log(this.model)
    return  JSON.stringify(this.model)
// return "[{'key':'name','value':'value'}]"
  }
}
