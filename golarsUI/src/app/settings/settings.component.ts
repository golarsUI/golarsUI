import { Component, OnInit } from '@angular/core';
import { CommonService } from '../services/common.service';
import { GolarsConstants } from '../constants/golarsconstants';
import { FolderService } from '../services/folder.service';

@Component({
  selector: 'golars-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  adminSelectedColumns: string[] = [];
  nonadminSelectedColumns: string[] = [];
  adminSelectedColumnValue="";
  nonAdminSelectedColumnValue="";
  showSuccessMessage=false;
  successMessage=null;
  data=[];

  constructor(private commonService: CommonService,private folderService:FolderService) { }

  ngOnInit() {
    var prefString = this.commonService.getTableAdminPreferences();
    var pefArray = prefString.split(GolarsConstants.SPLIT_STRING)
    for (var i = 0; i < pefArray.length; i++) {
      this.adminSelectedColumns.push(pefArray[i]);
    }
    prefString = this.commonService.getTableNonAdminPreferences();
    var pefArray = prefString.split(GolarsConstants.SPLIT_STRING)
    for (var i = 0; i < pefArray.length; i++) {
      this.nonadminSelectedColumns.push(pefArray[i]);
    }
  }
  saveUserSettings() {
    this.data=[];
    this.adminSelectedColumnValue="";
    this.nonAdminSelectedColumnValue="";
    for (var i = 0; i < this.adminSelectedColumns.length; i++) {
      this.adminSelectedColumnValue += this.adminSelectedColumns[i];
      if (i != this.adminSelectedColumns.length - 1)
        this.adminSelectedColumnValue += GolarsConstants.SPLIT_STRING
    }
    for (var i = 0; i < this.nonadminSelectedColumns.length; i++) {
      this.nonAdminSelectedColumnValue += this.nonadminSelectedColumns[i];
      if (i != this.nonadminSelectedColumns.length - 1)
        this.nonAdminSelectedColumnValue += GolarsConstants.SPLIT_STRING
    }
    this.data.push({key:"adminTableColumns",value:this.adminSelectedColumnValue});
    this.data.push({key:"nonAdminTableColumns",value:this.nonAdminSelectedColumnValue});
    console.log(this.data);
    this.folderService.saveTablePreferences(this.data)
    .subscribe(
        data => {
          this.showSuccessMessage=true;
          this.successMessage = "User Preferences saved successfully";
          this.commonService.updatePreferences(data);
          //   for(var i=0;i<=data.length;i++){
          //   localStorage.setItem(data[0].key, data[0].value);  
          // } 
                 },
        error => {
            console.log(error);
        });
  }

}
