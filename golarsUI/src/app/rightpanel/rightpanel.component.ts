import { Component, OnInit } from '@angular/core';
import { FolderService } from '../services/folder.service';
import { CommonService } from '../services/common.service';
import { GolarsConstants } from '../constants/golarsconstants';

@Component({
  selector: 'golars-rightpanel',
  templateUrl: './rightpanel.component.html',
  styleUrls: ['./rightpanel.component.css']
})
export class RightpanelComponent implements OnInit {

  docData=[];
  docName;
  treeNode;
  documentProperties=false;
  folderSelected=true;
  docURL: string = '';
  copyclicked=false;
  copyButtontitle="Copy URL";
  constructor(private folderService: FolderService,private commonService: CommonService) { }

  ngOnInit() {

    this.commonService.notifyObservable$.subscribe((treeNode) => {
      if(treeNode !== null && treeNode.type === "fetchSubFolders"){
        this.docData=[];
        this.folderSelected=true;
      }
      if(treeNode !== null && treeNode.node !== undefined && treeNode.type === "documentDetails" && treeNode.node.folder === false){
        this.copyclicked=false;
        this.copyButtontitle="Copy URL";
        this.documentProperties = true;
        this.folderSelected=false;
        this.docName = treeNode.node.label;
        this.docData=[];
        this.treeNode = treeNode;
        this.docData = treeNode.node.properties; 
        this.documentProperties = false;
        this.constructURL();
        // this.folderService.fetchDocumentDetails(treeNode.node.label)
        // .subscribe(
        //   fetchDocumentDetails => {
        //       this.documentProperties = false;
        //       this.docData = fetchDocumentDetails; 
        //       console.log(fetchDocumentDetails)
        //     },
        //     error => {
        //         console.log(error);
        //     });
      
      } else if(treeNode !== null && treeNode.node !== undefined && treeNode.type === "documentDetails" && treeNode.node.folder === true){
        this.docData=[];
        this.folderSelected=true;
      }
  });
  }
  constructURL(){
  this.docURL = location.origin+GolarsConstants.DOWNLOAD_DOC_URL+ encodeURI(this.docName);
  }
  getDocPropertiesValue(){
    if(this.docName == null) return "";
    return "File properties of "+this.docName;
  }
 
  }