import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from './services/authentication.service';
import { CommonService } from './services/common.service';
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
  constructor( private router: Router,private authenticationService: AuthenticationService,private commonService: CommonService){
console.log("constructor")
  }
  ngOnInit(){
    console.log( localStorage.getItem("currentUser"));
    if(localStorage.getItem("currentUser") === null)
      this.router.navigate(['/login']);
      else{
      this.loginSuccesful=true;
      this.router.navigate(['/']);
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
    if(treeNode !== null && treeNode !== undefined && treeNode.type === "fetchSubFolders"){
    if(treeNode.nodeName !== null){
    this.isNodeSelected= true;
    this.importFolder = treeNode.nodeName;
    }
    
    }
});

  }
  checkLoginSuccesful(){
    if(localStorage.getItem("currentUser") !== null){
      this.loginSuccesful=true;
    return this.loginSuccesful;
    }
  }
  logout(){
    this.loginSuccesful=false;
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }
}
