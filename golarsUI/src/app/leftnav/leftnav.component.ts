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
    treeLoading = true;
    newfolder = null;
    isNodeSelected = false;
    filderErrorMessage = null;
    isDocumentsRequired = false;
    selectedNode;
    items = [ ];
    selectedItemParentNode;
    createFolderFailureMessage = null;
    createFoldershowFailureMessage = false;
    nodeSelect(event) {
        // this.selectedNodeLabel = event.node.label;
        this.selectedItemParentNode = event.node.parent;
        event.node.expanded = !event.node.expanded;
        this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedNode, isDocumentsRequired: true });
        console.log("nodeSelect", event)
    }

    nodeRightClickSelect(event) {
        this.selectedNode = event.node;
        this.selectedItemParentNode = event.node.parent;
        if(event.node.id == GolarsConstants.ROOTID){
            this.items = [
                { label: 'New Folder', command: (event) => this.createNewFolder() }
            ];  
        }else{
            this.items = [
                { label: 'Import', command: (event) => this.ImportFile() },
                { label: 'New Folder', command: (event) => this.createNewFolder() },
                { label: 'Delete Folder', command: (event) => this.deleteFolder(event) }
            ];
        }
        this.selectedNode.expanded = true;
        this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedNode, isDocumentsRequired: true });
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
    deleteFolder(event) {
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
        var self= this;
        $('body').on('hide.bs.modal', '.modal', function($event){
            if($event.target.id!="create_Folder_Modal") return;
            self.newfolder = ""
            self.resetAlertMessages();
      
        })

        this.folderService.fetchFolders("-1",null, this.isDocumentsRequired,this.commonService.getUserName(),this.commonService.isAdmin()) // retrieve all thd parent folders
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.addFolderClass(element);
                    });
                    this.treeLoading=false;
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
        this.selectedNode.expanded = true;
        this.folderService.createFolder(subFolderName, this.getnerateFolderID(),true,this.commonService.getUserName())
            .subscribe(
                result => {
                    if(result == null){
                        this.createFoldershowFailureMessage=true;
                        this.createFolderFailureMessage="Folder Already Exists"
                    }else{
                    console.log("folder create", result)
                    if (this.selectedNode.children == null)
                        this.selectedNode.children = [];
                    this.selectedNode.children.push(result);
                    result.parent=this.selectedNode;
                    this.treeComponent.selection = result;
                    this.selectedNode = result;
                    this.addFolderClass(result)
                    $('#create_Folder_Modal').modal('hide');
                    this.newfolder = ""
                    this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedNode, isDocumentsRequired: true });
                }
                },
                error => {

                    console.log(error);
                });

    }
    deleteFolderOnConfirmation() {
        this.folderService.deleteFolder(this.selectedNode.id,this.selectedNode.parentid)
            .subscribe(
                folder => {
                    // this.treeComponent.
                    // this.selectedNode.parentid
                    var index = this.selectedItemParentNode.children.indexOf(this.selectedNode);
                   console.log(index);
                   this.selectedItemParentNode.children.splice(index,1);
                   this.treeComponent.selection = this.selectedItemParentNode;
                   this.selectedNode = this.selectedItemParentNode;
                  // header.slice(0, -1);

                    $('#folder_delete_model').modal('hide');
                    //   this.newfolder=""
                      this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedItemParentNode, isDocumentsRequired: true });
                },
                error => {

                    console.log(error);
                });
    }
    getnerateFolderID(){
        if(this.selectedNode.parentid.toLowerCase() != "null")
        return this.selectedNode.parentid+this.selectedNode.id;
        return this.selectedNode.id;
    }
    resetAlertMessages(){
        this.createFoldershowFailureMessage=false;
        this.createFolderFailureMessage=null
    }
}
