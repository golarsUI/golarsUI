import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from './services/authentication.service';
import { CommonService } from './services/common.service';
import { GolarsConstants } from './constants/golarsconstants';
declare var $:any;
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  loginSuccesful=false;
  isNodeSelected= false;
  importFolder;
  selectedNode;
  user;
  fullName;
  isAdmin=false;
  searchString;
  constructor( private router: Router,private authenticationService: AuthenticationService,private commonService: CommonService){
// console.log("constructor")
  }
  ngOnInit(){
    this.commonService.removeEditUser();
     this.user = (localStorage.getItem("currentUser"));
    // console.log( this.user);
    if(this.user === null){
      if(location.hash.indexOf('resetpassword') >= 0){
        this.setResetPasswordDummyUser();
            }else
      this.router.navigate(['/login']);
  }
      else{
        this.user = JSON.parse(localStorage.getItem("currentUser"))
        if(this.user.resetpassword != null && this.user.resetpassword == true){
          if(location.hash.indexOf('resetpassword') >= 0){
            this.setResetPasswordDummyUser();
          }
        }
        else if(this.user.newlyCreated == true)
         this.router.navigate(['changepassword']);
        else{
          if(localStorage.getItem("currentUser"))
          this.loginSuccesful=true;
          this.router.navigate(['/']);
        }
      }

      $(".navbar a").on("click", function(){

        var isDisabled =  this.className.indexOf("disabled") >=0;
      if(isDisabled == true)
      return;
        $(".navbar").find(".active").removeClass("active");
        $(this).parent().addClass("active");
     });
     $("#navbar_import").on("click",function(e){
      var isDisabled =  this.className.indexOf("disabled") >=0;
      e.preventDefault();
      if(isDisabled == true)
      return;
      $('#importModal').modal('show');
  })
  $("#navbar_import_dropdown").on("click",function(e){
    var isDisabled =  this.className.indexOf("disabled") >=0;
    e.preventDefault();
    if(isDisabled == true)
    return;
    $('#importModal').modal('show');
})
  
  $("#navbar_bulk_import").on("click",function(e){
    var isDisabled =  this.className.indexOf("disabled") >=0;
    e.preventDefault();
    if(isDisabled == true)
    return;
    $('#bulkImportModal').modal('show');
})
  $("#navbar_createNew").on("click",function(e){
    var isDisabled =  this.className.indexOf("disabled") >=0;
      e.preventDefault();
      if(isDisabled == true)
      return;
    $('#create_Folder_Modal').modal('show');
})
$("#navbar_delete_folder").on("click",function(e){
  e.preventDefault();
  $('#folder_delete_model').modal('show');
})

  

  this.commonService.notifyObservable$.subscribe((treeNode) => {
    if(treeNode !== null && treeNode !== undefined && treeNode !== undefined &&  treeNode.node !== null &&  treeNode.node !== undefined && treeNode.type === "fetchSubFolders"){
      this.selectedNode = treeNode.node;
      this.isNodeSelected= true;
    if(treeNode.node.label !== null && treeNode.node.id != GolarsConstants.ROOTID){
    
    this.importFolder = treeNode.node.label;
    }
    
    }
});

  }
  getFullName(){
    if(this.user == null)
      return "";
      else
      return this.user.fullName
    
  }
  

  checkValidUser(){
    if(this.router.url.indexOf( 'resetpassword') >= 0){
    
      this.setResetPasswordDummyUser();
  
    }else if(localStorage.getItem("currentUser") === null)
     this.router.navigate(['/login']);
     else {
      this.user = JSON.parse(localStorage.getItem("currentUser"))
      if(this.user!==null  && this.user.resetpassword !=null && this.user.resetpassword == true){
        // var queryParamArray[]:any = this.user.queryParam.split("=");
      this.router.navigate(['/resetpassword'], { queryParams: { username: this.user.username } });
      }
      if(this.user!==null  && this.user.newlyCreated == true)
      this.router.navigate(['changepassword']);
      else if(this.user!==null && this.user.admin &&(this.router.url == '/users' || this.router.url == '/resetpassword' || this.router.url == '/newuser'|| this.router.url == '/settings' || this.router.url == '/configuration'))
      this.router.navigate([this.router.url]);
      else
      this.router.navigate(['']);
     }
  }
  checkLoginSuccesful(){
    if(localStorage.getItem("currentUser") !== null){
     
      this.user = JSON.parse(localStorage.getItem("currentUser"))
      
      if(this.user!==null  && (this.user.newlyCreated == true || this.user.resetpassword))
      return ;
      this.loginSuccesful=true;
      this.fullName = this.user.fullName;
      this.isAdmin = this.user.admin;
    return this.loginSuccesful;
    }
  }
  logout(){
    this.loginSuccesful=false;
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }
  checkImportDisabled(){
    if(this.selectedNode!=undefined && this.selectedNode.id !==undefined)
      return !(this.isNodeSelected && this.selectedNode.id != GolarsConstants.ROOTID && (this.isAdmin || this.selectedNode.username.toUpperCase().indexOf(this.user.username.toUpperCase())>=0));
    else
      return !this.isNodeSelected
  
  }
  checkCreateButtonDisabled(){
    if(this.selectedNode!=undefined && this.selectedNode.id !==undefined)
      return !(this.isNodeSelected && (this.isAdmin || this.selectedNode.username.toUpperCase().indexOf(this.user.username.toUpperCase())>=0));
    else
      return !this.isNodeSelected
  }

  checkSearchEnabled(){
    if(this.searchString!=null && this.searchString.trim().length != 0)
    return false;
    return true;
  }
  fetchSearchResults(){
     this.commonService.notify({ type: 'clearRightSidePanel', node: "", isDocumentsRequired: true });
    this.commonService.notify({ type: 'fetchSearchResults', searchString: this.searchString});
  }
  setResetPasswordDummyUser(){
    var queryParam = location.hash.substring(location.hash.indexOf( '?')+1,location.hash.length);
        var queryParamArray= queryParam.split("=")
        var obj ={dummyuser:"dummyuser",resetpassword:true,username:queryParamArray[1]};
        localStorage.setItem("currentUser",JSON.stringify(obj));
        this.router.navigate(['/resetpassword'], { queryParams: { username: queryParamArray[1] } });

  }

}
