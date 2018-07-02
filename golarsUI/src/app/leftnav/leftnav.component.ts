import { Component, OnInit, ViewChild } from '@angular/core';
import { TreeNode, TreeModule, Tree } from 'primeng/primeng';
import { FolderService } from '../services/folder.service';
import { CommonService } from '../services/common.service';
import { GolarsConstants } from '../constants/golarsconstants';

declare var $: any;
@Component({
    selector: 'golars-leftnav',
    templateUrl: './leftnav.component.html',
    styleUrls: ['./leftnav.component.css']
})
export class LeftnavComponent implements OnInit {
    @ViewChild('leftNavTree')  treeComponent: Tree;
    constructor(private folderService: FolderService, private commonService: CommonService) {


    }
    folderData

    newfolder = null;
    isNodeSelected = false;
    filderErrorMessage = null;
    isDocumentsRequired = false;
    selectedNode;
    items = [
        { label: 'Import', command: (event) => this.ImportFile() },
        { label: 'New Folder', command: (event) => this.createNewFolder() },
        { label: 'Delete Folder', command: (event) => this.deleteFolder() }
    ];
    nodeSelect(event) {
        // this.selectedNodeLabel = event.node.label;
        event.node.expanded = !event.node.expanded;
        this.commonService.notify({ type: 'fetchSubFolders', nodeName: this.selectedNode.label, isDocumentsRequired: true });
        this.isDocumentsRequired
        console.log("nodeSelect", event)
    }

    nodeRightClickSelect(event) {
        this.selectedNode = event.node;
        this.selectedNode.expanded = true;
        this.commonService.notify({ type: 'fetchSubFolders', nodeName: this.selectedNode.label, isDocumentsRequired: true });
        this.isDocumentsRequired
        console.log("nodeSelect", event)
    }

    ImportFile() {
        $('#importModal').modal('show');
    }
    createNewFolder() {
        $('#create_Folder_Modal').modal('show');

        console.log()
    }
    deleteFolder() {
        $('#folder_delete_model').modal('show');
    }
    nodeUnselect(event) {
        console.log("nodeUnselect", event)
        event.node.expanded = !event.node.expanded;
    }
    isCreateDisable() {
        if (this.newfolder != null && this.newfolder.value > 0)
            return false;
        return true;
    }
    ngOnInit() {

        this.folderService.fetchFolders("-1", this.isDocumentsRequired) // retrieve all thd parent folders
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.addFolderClass(element);
                    });
                    this.folderData = data;
                    // data.forEach(function(entry) {
                    //     console.log(entry);
                    // })
                },
                error => {
                    console.log(error);
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
    createFolder(subFolderName) {
        this.selectedNode.expanded = true
        this.folderService.createFolder(subFolderName, this.selectedNode.id)
            .subscribe(
                folder => {
                    console.log("folder create", folder)
                    if (this.selectedNode.children == null)
                        this.selectedNode.children = [];
                    this.selectedNode.children.push(folder);

                    this.treeComponent.selection = folder;
                    this.selectedNode = folder;
                    this.addFolderClass(folder)
                    $('#create_Folder_Modal').modal('hide');
                    this.newfolder = ""
                    this.commonService.notify({ type: 'fetchSubFolders', nodeName: this.selectedNode.label, isDocumentsRequired: true });
                },
                error => {

                    console.log(error);
                });

    }
    deleteFolderOnConfirmation() {
        this.folderService.deleteFolder(this.selectedNode.id)
            .subscribe(
                folder => {
                    $('#folder_delete_model').modal('hide');
                    //   this.newfolder=""
                    //   this.commonService.notify({ type: 'fetchSubFolders', nodeName: this.selectedNode.label, isDocumentsRequired: true });
                },
                error => {

                    console.log(error);
                });
    }
}
