import { Component, OnInit, ViewChild } from '@angular/core';
import { UserService } from '../services/user.service';
import { FormGroup, NgForm } from '@angular/forms';
import { CommonService } from '../services/common.service';
import { FolderService } from '../services/folder.service';
import { GolarsConstants } from '../constants/golarsconstants';

@Component({
  selector: 'golars-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  model: any = {};
  constructor(private userService: UserService,private commonService:CommonService,private folderService:FolderService) { }
  showSuccessMessage = false;
  showFailureMessage = false;
  successMessage = null;
  failureMessage = null;
  disableRegisterButton=false;
  selectedFiles;
  folderData:any;
  treeLoading = true;
  @ViewChild('f') formElem: NgForm;
  ngOnInit() {
    this.folderService.fetchFolders("-1",null, false,this.commonService.getUserName(),this.commonService.isAdmin()) // retrieve all thd parent folders
    .subscribe(
        data => {
            data.forEach(element => {
                this.addFolderClass(element);
            });
            this.treeLoading=false;
            this.folderData = data;
            // data.forEach(function(entry) {
            //     console.log(entry);
            // })
        },
        error => {
            console.log(error);
        });

  }

  register() {
    this.resetSuccessAndFailureMessages();
    this.model.newlyCreated = true;
    this.userService.registerUser(this.model)
      .subscribe(
        result => {
          if (result == true) {
            
            // this.model=[];
            this.showSuccessMessage = true;
            this.successMessage = "User Created Successfully.";
            this.disableRegisterButton = true;
          //   this.model=[];
          //   this.formElem.form.reset();
          //   Object.keys(this.formElem.controls).forEach(control => {
          //     this.formElem.controls[control].setErrors(null);
          //     this.formElem.controls[control].markAsPristine();
             
          // });
          } else {
            this.showFailureMessage = true;
            this.failureMessage = "User Already Exists.";
          }
          console.log(result)
        },
        error => {
          console.log(error)
        });
  }
  addFolderClass(element) {
    element.expandedIcon = GolarsConstants.FOLDER_OPEN_ICON;
    element.collapsedIcon = GolarsConstants.FOLDER_CLOSE_ICON;
    if (element.children != null && element.children.length > 0)
        element.children.forEach(element => {
            this.addFolderClass(element);
        });

}
fillPermissionField(){
  this.model.filePermission="";
  this.model.permissonFolderID=""
  for(var i=0;i<this.selectedFiles.length;i++){
        this.model.filePermission += this.selectedFiles[i].label;
        this.model.permissonFolderID+=this.selectedFiles[i].id;
        if(i<this.selectedFiles.length-1){
          this.model.filePermission +=",";
          this.model.permissonFolderID+=",";
        }
      }
}
  resetSuccessAndFailureMessages() {
    this.showSuccessMessage = false;
    this.showFailureMessage = false;
    this.successMessage = null;
    this.failureMessage = null;
    this.disableRegisterButton=false;
  }
}
