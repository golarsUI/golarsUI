import { Component, OnInit } from '@angular/core';
import { FolderService } from '../services/folder.service';
import { CommonService } from '../services/common.service';

@Component({
  selector: 'golars-rightpanel',
  templateUrl: './rightpanel.component.html',
  styleUrls: ['./rightpanel.component.css']
})
export class RightpanelComponent implements OnInit {

  docData;
  docName;
  treeNode;
  constructor(private folderService: FolderService,private commonService: CommonService) { }

  ngOnInit() {
    this.commonService.notifyObservable$.subscribe((treeNode) => {
      if(treeNode !== null && treeNode.node !== undefined && treeNode.type === "documentDetails"){
        this.docName = treeNode.node.label;
        this.docData=[];
        this.treeNode = treeNode;
        this.folderService.fetchDocumentDetails(treeNode.node.label)
        .subscribe(
          fetchDocumentDetails => {
              // data.fo
              this.docData = fetchDocumentDetails; 
              console.log(fetchDocumentDetails)
            },
            error => {
                console.log(error);
            });
      
      }
  });
  }
  getDocPropertiesValue(){
    if(this.docName == null) return "";
    return "Properties of "+this.docName;
  }
  }