import { Component, OnInit } from '@angular/core';
import { CommonService } from '../services/common.service';
import { Subscription } from 'rxjs';
import { FolderService } from '../services/folder.service';
import { GolarsConstants } from '../constants/golarsconstants';
import { environment } from '../../environments/environment';
declare var $: any;

@Component({
  selector: 'golars-middlepane',
  templateUrl: './middlepane.component.html',
  styleUrls: ['./middlepane.component.css']
})
export class MiddlepaneComponent implements OnInit {
  folderData;
  selectedNode;
  folderDetailstreeLoading = true;
  treeLoadingProgress = false;
  golarsServer = environment.server;
  cols = [];
  selectedDocumet=[];
  leftMenuSelectedNode;
  applyIconsColor = false;
  tableColumnArray =['fid','fecilityName','docUpdateDate','docDate','stateProgram','docTypes', 'scopeOfWork','active']
  tableColumnMapping = {
    'fid': 'FID',
    'fecilityName': 'Facility Name',
    'docUpdateDate': 'Date Document Uploaded',
    'docDate': 'Document Date',
    'stateProgram': 'State Program',
    'docTypes': 'Document Type',
    'scopeOfWork': 'Scope of Work',
    'active': 'Is Active'
  }
  constructor(private folderService: FolderService, private commonService: CommonService) { }

  ngOnInit() {
    this.commonService.notifyObservable$.subscribe((treeNode) => {
      if (treeNode !== null && treeNode !== undefined &&  treeNode.node !== undefined && treeNode.type === "fetchSubFolders") {
        // console.log("fetchSubFolders ",this.selectedNode);  
        this.treeLoadingProgress = true;
        this.folderData = [];
        this.selectedDocumet[0]=this.selectedNode;
        this.leftMenuSelectedNode = treeNode;
        this.fetchSubFolders();
       
      }else if(treeNode !== null && treeNode !== undefined&& treeNode.searchString !== undefined && treeNode.type === "fetchSearchResults"){
        this.folderService.searchResults(treeNode.searchString,this.commonService.getUserName(),this.commonService.isAdmin()).subscribe(
          data => {
// console.log("search results came ",data);  
            this.getColumns();
            data = this.constructFolderFirst(data);
            this.folderData = this.constructTableData(data);
            this.folderDetailstreeLoading = false;
            this.treeLoadingProgress = false;
            // console.log("search results came ",this.folderData);  
          },
          error => {
            console.log(error);
          });
      }

    });
  }

  fetchSubFolders() {
    this.folderService.fetchFolders(this.leftMenuSelectedNode.node.id, this.leftMenuSelectedNode.node.parentid, this.leftMenuSelectedNode.isDocumentsRequired, this.commonService.getUserName(), this.commonService.isAdmin())
      .subscribe(
        data => {

          this.getColumns();
          data = this.constructFolderFirst(data);
          this.folderData = this.constructTableData(data);
          this.folderDetailstreeLoading = false;
          this.treeLoadingProgress = false;
        },
        error => {
          console.log(error);
        });
  }
  constructTableData(data) {
    for (var i = 0; i < data.length; i++) {
      if (data[i].folder == true)
        continue;
      data[i].properties = JSON.parse(data[i].properties);
      if(data[i].properties.docUpdateDate!=null){
        data[i].properties.docUpdateDate = this.commonService.getFormatteDate(data[i].properties.docUpdateDate);
      }
      if(data[i].properties.docDate!=null){
        data[i].properties.docDate = this.commonService.getFormatteDate(data[i].properties.docDate);
      }
    }
    return data;
  }
  constructFolderFirst(data) {
    var folderList = []
    var docList = [];
    for (var i = 0; i < data.length; i++) {
      if (data[i].folder == true)
        folderList.push(data[i])
      else
        docList.push(data[i]);
    }
    docList.forEach(function (element) {
      folderList.push(element)
    });
    // folderList.(docList)
    return folderList;

  }
  // deleteRow($event){
  //   setTimeout(() => {
  //    this.deleteDocument();

  //   }, 500);

  // }
  deleteDocument() {
    this.folderService.deleteFolder(this.selectedNode.id, this.selectedNode.parentid, this.commonService.getUserName(), this.commonService.isAdmin())
      .subscribe(
        folder => {
          this.commonService.notify({ type: 'refreshFolder', node: this.leftMenuSelectedNode.node, isDocumentsRequired: true });

          //   // this.treeComponent.
          //   // this.selectedNode.parentid
          //   var index = this.folderData.children.indexOf(this.selectedNode);
          //  console.log(index);
          //  this.folderData.children.splice(index,1);
          $('#middle_pane_folder_delete_model').modal('hide');
        },
        error => {

          console.log(error);
        });
  }

  nodeSelect(event) {
    this.selectedNode = event.data;
    this.commonService.notify({ type: 'documentDetails', node: event.data, isDocumentsRequired: true });
    // console.log("middle nodeSelect", event);
    this.applyIconsColor = true;
  }
  nodeUnselect(event) {
    // console.log("nodeUnselect", event)
  }
  addFolderClass(element) {
    element.expandedIcon = GolarsConstants.FOLDER_OPEN_ICON;
    element.collapsedIcon = GolarsConstants.FOLDER_CLOSE_ICON;
    if (element.children != null && element.children.length > 0)
      element.children.forEach(element => {
        this.addFolderClass(element);
      });

  }
  getColumns() {
    this.cols = [];
    var prefString = "";
    if (this.commonService.isAdmin())
      prefString = this.commonService.getTableAdminPreferences();
    else
      prefString = this.commonService.getTableNonAdminPreferences()
    var pefArray = prefString.split(GolarsConstants.SPLIT_STRING)
    for (var i = 0; i < this.tableColumnArray.length; i++) {
      if(this.tableColumnArray.indexOf(pefArray[i])>=0)
      this.cols.push({ field: this.tableColumnArray[i], header: this.tableColumnMapping[this.tableColumnArray[i]] });
    }
  }

  deleteFolderOrDocument() {
    $('#middle_pane_folder_delete_model').modal('show');
  }
  editDocument(rowData){
    this.commonService.setDocData(JSON.stringify(rowData))
    $('#importModal').modal('show');
    // console.log(rowData)
  }
  customSort(event) {
    event.data.sort((data1, data2) => {
      let value1, value2;
      if(event.field == 'label'){
        value1 = data1.label;
        value2 = data2.label;
      }else{
      if (data1 != null ) {
        if (data1.properties != null && data1.properties[event.field] !== null)
          value1 = data1.properties[event.field];
        else
          value1 = data1[event.field];
      }
      if(data2 != null){
        if (data2.properties != null && data2.properties[event.field] !== null)
          value2 = data2.properties[event.field];
        else
          value2 = data2[event.field];
      }
    }
      let result = null;
        if(data1.folder && !data2.folder)
        return -1;
        if(data2.folder && !data1.folder)
        return 1;
      if (value1 == null && value2 != null)
        result = -1;
      else if (value1 != null && value2 == null)
        result = 1;
      else if (value1 == null && value2 == null)
        result = 0;
      else if (typeof value1 === 'string' && typeof value2 === 'string')
        result = (data1.folder-data2.folder) ||value1.localeCompare(value2);
      else
        result = (value1 < value2) ? -1 : (value1 > value2) ? 1 : 0;

      return (event.order * result);
    });
  }

}
