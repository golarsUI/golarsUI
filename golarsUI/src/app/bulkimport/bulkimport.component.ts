import { Component, OnInit, ViewChild } from '@angular/core';
import { FileUpload } from 'primeng/primeng';
import { ImportService } from '../services/import.service';
import { CommonService } from '../services/common.service';
declare var $: any;
@Component({
  selector: 'golars-bulkimport',
  templateUrl: './bulkimport.component.html',
  styleUrls: ['./bulkimport.component.css']
})
export class BulkimportComponent implements OnInit {
  @ViewChild('fileInput') fileInput: FileUpload;
  frmData = new FormData();
  uploadedFiles: any[] = [];
  showFileSelectErrorMessage = false;
  fileSelectErrorMessage = null
  disableImportButton = false;
  showSuccessMessage = false;
  successMessage = null;
  constructor(private importService: ImportService, private commonService: CommonService) { }

  ngOnInit() {
    var self = this;
    $('body').on('hide.bs.modal', '.modal', function ($event) {
      if ($event.target.id != "bulkImportModal") return;

      self.commonService.removeEditUser();
      self.showSuccessMessage = false;
      self.successMessage = null;
      self.showFileSelectErrorMessage = false;
      self.fileSelectErrorMessage = null;
      if (self.fileInput != null)
        self.fileInput.files = [];



    })
  }

  importBulkDocuments() {
    if (this.fileInput.files.length == 0) {
      this.showFileSelectErrorMessage = true;
      this.fileSelectErrorMessage = "Please select at least one file to Import"
      return;
    }
    if (this.fileInput.files.length > 1) {
      this.showFileSelectErrorMessage = true;
      this.fileSelectErrorMessage = "Please select only one file to Import"
      return;
    }
    for (let file of this.fileInput.files) {
      this.frmData.append("fileUpload", file);
      this.frmData.append("userName", this.commonService.getUserName());
      this.importService.bulkImportDocuments(this.frmData)
        .subscribe(
          message => {
            console.log(message)
            if (message == -1) {
              this.showFileSelectErrorMessage = true;
              this.fileSelectErrorMessage = "Please select valid file to import"
            }
            else if (message == 10) {
              this.showFileSelectErrorMessage = true;

              this.fileSelectErrorMessage = "The total URL's are not maching with the total count. Please check URL columns values.";
            } else if (message == 0) {
              this.showSuccessMessage = true;
              this.successMessage = "Bulk import in progress. You will be notified by mail once import is completed";
            }


          },
          error => {

            console.log(error);
          });

    }



    // this.msgs = [];
    // this.msgs.push({severity: 'info', summary: 'File Uploaded', detail: ''});
  }
  onBasicUpload($event) {
    this.showFileSelectErrorMessage = false;
    this.fileSelectErrorMessage = null;
    this.showSuccessMessage = false;
    this.successMessage = null;
  }
}
