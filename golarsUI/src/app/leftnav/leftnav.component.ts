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
    hideContextMenu=true;
    selectedItemParentNode;
    createFolderFailureMessage = null;
    createFoldershowFailureMessage = false;
    refreshInProgress=false;
    nodeSelect(event) {
        // this.selectedNodeLabel = event.node.label;
        this.selectedItemParentNode = event.node.parent;
        event.node.expanded =true;
        this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedNode, isDocumentsRequired: true });
        // console.log("nodeSelect", event)


    }
    selectTreeNode(children,id){
        for(var i=0;i<children.length;i++){
            var folder = children[i];
            if(folder.id == id){
                return folder;
                // folder.expanded = true;
                // this.treeComponent.selection = folder;
            //    this.expandChildren(this.treeComponent.selection)
            }
            
            else{
            //   folder.expanded=true;
           return  this.selectTreeNode(folder.children,id)
        }

        }
    }
    expandParent(node:TreeNode){
        node.expanded=true;
        if(node.parent){
          if(node.parent!=null)
          this.expandParent(node.parent)
         
        }
      }

    nodeRightClickSelect(event) {
        this.hideContextMenu=true;
        this.items = [];
        this.selectedNode = event.node;
        this.selectedItemParentNode = event.node.parent;
        if(this.commonService.isAdmin() || this.selectedNode.username.toUpperCase().indexOf(this.commonService.getUserName().toUpperCase())>=0)
        this.items = [
            { label: 'New Folder', command: (event) => this.createNewFolder() }
        ]; 
        if(event.node.id != GolarsConstants.ROOTID){
        if(this.commonService.isAdmin() ){
            this.items.push({ label: 'Import', command: (event) => this.ImportFile() });
            this.items.push({ label: 'Delete Folder', command: (event) => this.deleteFolder(event) });
           
        } else  if(this.commonService.isAdmin() || this.selectedNode.username.toUpperCase().indexOf(this.commonService.getUserName().toUpperCase())>=0){
            this.items.push({ label: 'Import', command: (event) => this.ImportFile() });
        }
    }
        this.selectedNode.expanded = true;
        this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedNode, isDocumentsRequired: true });
        // this.isDocumentsRequired
        // console.log("nodeSelect", event)
        if( this.items.length>0){
            this.hideContextMenu=false;
        }
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
        this.commonService.notifyObservable$.subscribe((treeNode) => {

            if (treeNode !== null && treeNode !== undefined && treeNode.node !== undefined && treeNode.type === "documentDetails") {
                var parentId = treeNode.node.parentid;
                if (parentId != null)
                    parentId = parentId.substring(parentId.length - 4);
                this.folderData[0].expanded = true;
                var folder;
                var folderId = parseInt(parentId);
                for (var i = 0; i < this.folderData[0].children.length; i++) {
                    if(this.folderData[0].children[i].id == folderId){
                        folder =this.folderData[0].children[i];
                        break; 
                    }
                    folder = this.selectTreeNode(this.folderData[0].children[i].children, folderId);
                    if (folder != null)
                        break;
                }
                if (folder != null){
                    folder.expanded=true;
                    this.treeComponent.selection = folder;
                    this.expandParent(folder)
                }
            } else if (treeNode !== null && treeNode !== undefined && treeNode.node !== undefined && treeNode.type === "refreshFolder") {
                    if(!this.refreshInProgress){
                    this.refreshInProgress = true;
                this.folderService.fetchFolders(treeNode.node.id,treeNode.node.parentid, false,this.commonService.getUserName(),this.commonService.isAdmin()) // retrieve all thd parent folders
                .subscribe(
                    data => {
                        data.forEach(element => {
                            this.addFolderClass(element,null);
                        });
                        // this.treeLoading=false;
                        treeNode.node.children = data;
                        this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedNode, isDocumentsRequired: true });
                        // data.forEach(function(entry) {
                        //     console.log(entry);
                        // })
                        this.refreshInProgress = false;
                    },
                    error => {
                        console.log(error);
                    });
            }
        }
        });
        this.folderService.fetchFolders("-1",null, this.isDocumentsRequired,this.commonService.getUserName(),this.commonService.isAdmin()) // retrieve all thd parent folders
            .subscribe(
                data => {
                    data.forEach(element => {
                        this.addFolderClass(element,null);
                    });
                    this.treeLoading=false;
                    this.folderData = data;
                    this.folderData[0].expanded = true;
                    // data.forEach(function(entry) {
                    //     console.log(entry);
                    // })
                },
                error => {
                    console.log(error);
                });
    }

    addFolderClass(element,parent) {
        element.expandedIcon = GolarsConstants.FOLDER_OPEN_ICON;
        element.collapsedIcon = GolarsConstants.FOLDER_CLOSE_ICON;
        if(parent != null)
        element.parent = parent;
        parent = element;
        if (element.children != null && element.children.length > 0)
            element.children.forEach(element => {
                element.parent = parent;
                this.addFolderClass(element,parent);
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
                    this.addFolderClass(result,result.parent)
                    $('#create_Folder_Modal').modal('hide');
                    this.newfolder = "";
                    this.commonService.notify({ type: 'fetchSubFolders', node: this.selectedNode, isDocumentsRequired: true });
                }
                },
                error => {

                    console.log(error);
                });

    }
    deleteFolderOnConfirmation() {
        this.folderService.deleteFolder(this.selectedNode.id,this.selectedNode.parentid,this.commonService.getUserName(),this.commonService.isAdmin())
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
