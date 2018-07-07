import { Component, OnInit } from '@angular/core';
import { CommonService } from '../services/common.service';
import { Subscription } from 'rxjs';
import { FolderService } from '../services/folder.service';
import { GolarsConstants } from '../constants/golarsconstants';
import { environment } from '../../environments/environment';

@Component({
  selector: 'golars-middlepane',
  templateUrl: './middlepane.component.html',
  styleUrls: ['./middlepane.component.css']
})
export class MiddlepaneComponent implements OnInit {
  folderData;
  selectedNode;
  folderDetailstreeLoading = true;
  treeLoadingProgress=false;
  golarsServer = environment.server;
  cols=[];
  selectedDocumet;
  contextMenuItems;
  tableColumnMapping={
    'docUpdateDate':'Date Document Updated',
    'fecilityName':'Facility Name',
    'docDate':'Document Date',
    'fid':'FID',
    'stateProgram':'State Program',
    'active':'Is Active',
    'docTypes':'Document Type',
    'scopeOfWork':'Scope of Work'
  }
  constructor(private folderService: FolderService, private commonService: CommonService) { }

  ngOnInit() {
    this.commonService.notifyObservable$.subscribe((treeNode) => {
      if (treeNode !== null && treeNode.node !== undefined && treeNode.type === "fetchSubFolders") {
        this.treeLoadingProgress = true;
        this.folderData=[];
        this.selectedDocumet=[];
        this.folderService.fetchFolders(treeNode.node.id,treeNode.node.parentid, treeNode.isDocumentsRequired,this.commonService.getUserName(),this.commonService.isAdmin())
          .subscribe(
            data => {

              this.getColumns();
              this.folderData =  this.constructTableData(data)
              this.folderDetailstreeLoading=false;
              this.treeLoadingProgress = false;
            },
            error => {
              console.log(error);
            });

      }
    });
    this.contextMenuItems = [
      { label: 'Delete', command: (event) => this.deleteDocument() }
  ];
  }
  constructTableData(data){
    for(var i=0;i<data.length;i++){
      if(data[i].folder== true)
      continue;
      data[i].properties = JSON.parse(data[i].properties);
}
return data;
  }
  deleteDocument() {
    this.folderService.deleteFolder(this.selectedDocumet.id,this.selectedDocumet.parentid)
        .subscribe(
            folder => {
                // this.treeComponent.
                // this.selectedNode.parentid
                var index = this.folderData.children.indexOf(this.selectedNode);
               console.log(index);
               this.folderData.children.splice(index,1);
            },
            error => {

                console.log(error);
            });
}

  nodeSelect(event) {
  this.selectedNode = event.data;
    this.commonService.notify({ type: 'documentDetails', node: event.data, isDocumentsRequired: true });
    console.log("middle nodeSelect", event)
  }
  nodeUnselect(event) {
    console.log("nodeUnselect", event)
  }
  addFolderClass(element) {
    element.expandedIcon = GolarsConstants.FOLDER_OPEN_ICON;
    element.collapsedIcon = GolarsConstants.FOLDER_CLOSE_ICON;
    if (element.children != null && element.children.length > 0)
      element.children.forEach(element => {
        this.addFolderClass(element);
      });

  }
  getColumns(){
    this.cols=[];
    var prefString = this.commonService.getTablePreferences();
    var pefArray =prefString.split(GolarsConstants.SPLIT_STRING)
    for(var i=0;i<pefArray.length;i++){
      this.cols.push({ field: pefArray[i], header: this.tableColumnMapping[pefArray[i]] });
    }
  //   this.cols =
    
  //   [
  //     { field: 'label', header: 'Label' },
  //     { field: 'year', header: 'Year' },
  //     { field: 'brand', header: 'Brand' },
  //     { field: 'color', header: 'Color' }
  // ];
  }
}
