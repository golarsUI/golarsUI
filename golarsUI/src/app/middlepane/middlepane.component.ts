import { Component, OnInit } from '@angular/core';
import { CommonService } from '../services/common.service';
import { Subscription } from 'rxjs';
import { FolderService } from '../services/folder.service';
import { GolarsConstants } from '../constants/golarsconstants';

@Component({
  selector: 'golars-middlepane',
  templateUrl: './middlepane.component.html',
  styleUrls: ['./middlepane.component.css']
})
export class MiddlepaneComponent implements OnInit {
  folderData;
  selectedNode;
  constructor(private folderService: FolderService, private commonService: CommonService) { }

  ngOnInit() {
    this.commonService.notifyObservable$.subscribe((treeNode) => {
      if (treeNode !== null && treeNode !== undefined && treeNode.type === "fetchSubFolders") {
        this.folderService.fetchFolders(treeNode.nodeName, treeNode.isDocumentsRequired)
          .subscribe(
            data => {
              data.forEach(element => {
                if (element.folder)
                  this.addFolderClass(element);
              });
              this.folderData = data;
            },
            error => {
              console.log(error);
            });

      }
    });
  }
  nodeSelect(event) {

    this.commonService.notify({ type: 'documentDetails', nodeName: event.node.label, isDocumentsRequired: true });
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
