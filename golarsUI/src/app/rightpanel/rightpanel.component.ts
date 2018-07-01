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
  constructor(private folderService: FolderService,private commonService: CommonService) { }

  ngOnInit() {
    this.commonService.notifyObservable$.subscribe((treeNode) => {
      if(treeNode !== null && treeNode !== undefined && treeNode.type === "documentDetails"){
        this.docName = treeNode.nodeName;
        this.folderService.fetchDocumentDetails(this.docName)
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