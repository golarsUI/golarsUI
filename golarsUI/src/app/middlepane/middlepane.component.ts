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
  constructor(private folderService: FolderService, private commonService: CommonService) { }

  ngOnInit() {
    this.commonService.notifyObservable$.subscribe((treeNode) => {
      if (treeNode !== null && treeNode.node !== undefined && treeNode.type === "fetchSubFolders") {
        this.treeLoadingProgress = true;
        this.folderData=[];
        this.folderService.fetchFolders(treeNode.node.id,treeNode.node.parentid, treeNode.isDocumentsRequired)
          .subscribe(
            data => {
              data.forEach(element => {
                if (element.folder)
                  this.addFolderClass(element);
              });
              this.folderData = data;
              this.folderDetailstreeLoading=false;
              this.treeLoadingProgress = false;
            },
            error => {
              console.log(error);
            });

      }
    });
  }
  nodeSelect(event) {

    this.commonService.notify({ type: 'documentDetails', node: event.node, isDocumentsRequired: true });
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
}
