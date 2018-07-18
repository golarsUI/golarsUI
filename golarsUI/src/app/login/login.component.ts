import { Injectable, Component, OnInit } from '@angular/core';
import {HttpClient, HttpParams, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs/Rx';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { FolderService } from '../services/folder.service';
import { GolarsConstants } from '../constants/golarsconstants';
declare var $:any;
@Component({
  selector: 'golars-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  model: any = {};
  cols;
loginData:string=""
loginContentURL=GolarsConstants.DEFAULT_LOGIN_CONTENT_URL;
returnUrl: string;
forgotemail;
  constructor(private route: ActivatedRoute,
    private router: Router, private folderService:FolderService,
    private authenticationService: AuthenticationService) { }
    showResetPasswordMessage =false;
    showResetPasswordFaiedMessage = false;
    resetPasswordFailedMessage =""
    resetPasswordMessage ="";
loginErrorMessage=null;
requestSent=false;
  ngOnInit() {
     // reset login status
     this.authenticationService.logout();
     localStorage.removeItem("currentUser");
      // get return url from route parameters or default to '/'
      this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

      this.fetchLoginPreferences(true);
      var self = this;
      $('body').on('hide.bs.modal', '.modal', function($event){
        if($event.target.id!="resetModal") return;
        self.resetSuccessAndFailureMessages();
  
    })
    }
    fetchLoginPreferences(admin){
      this.folderService.fetchTablePreferences(true)
      .subscribe(
          data => {
              for(var i=0;i<data.length;i++){
                  if(data[i].key == GolarsConstants.LOGIN_CONTENT_URL){
                  this.loginContentURL= data[i].value;
                  break;
                }
                } 
                    },
          error => {
              console.log(error);
          });
  }

  login() {
    this.authenticationService.login(this.model.username, this.model.password)
        .subscribe(
            data => {
              if(data.loginsuccess==true){
                if(data.newlyCreated == true){
                  this.router.navigate(['changepassword']);
                }
                else
                this.router.navigate([this.returnUrl]);
              }
               else
               this.displayErrorMessage();
            },
            error => {
              this.loginErrorMessage="Invalid Username or Password";
                console.log(error);
            });
}
displayErrorMessage(){
  this.loginErrorMessage="Invalid Username or Password";
}
  getHeaders(){
    var httpheaders = new HttpHeaders();
    httpheaders = httpheaders.set('Content-Type', 'application/json').set('Accept','application/json');
    return httpheaders;
  }
  private handleError(error: any) {
    console.log(error)
    return Observable.throw(error);
  }
  handleResponse(res){
    console.log("test "+res);
  }
  forgotPassword(){
    console.log(location);
    this.requestSent=true;
    this.authenticationService.forgotPassword(this.forgotemail,this.resetPasswordLink())
    .subscribe(
        result => {
          this.requestSent=false;
          if(result === true){
            this.showResetPasswordFaiedMessage = false;
            this.resetPasswordFailedMessage = "";
         this.showResetPasswordMessage = true;
         this.resetPasswordMessage = "Reset password link send successfully";
        }else{
          this.showResetPasswordMessage = false;
          this.resetPasswordMessage = "";
          this.showResetPasswordFaiedMessage = true;
          this.resetPasswordFailedMessage = "Email Address is not present";
        }
        },
        error => {
            console.log(error);
        });

  }
  resetSuccessAndFailureMessages(){
    this.showResetPasswordMessage =false;
    this.showResetPasswordFaiedMessage = false;
    this.resetPasswordFailedMessage =""
    this.resetPasswordMessage ="";
  }
  resetPasswordLink(){
    return location.origin+GolarsConstants.RESET_PASSWORD_LINK;
}
}
