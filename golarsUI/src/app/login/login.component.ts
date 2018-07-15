import { Injectable, Component, OnInit } from '@angular/core';
import {HttpClient, HttpParams, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs/Rx';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { FolderService } from '../services/folder.service';
import { GolarsConstants } from '../constants/golarsconstants';

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
  constructor(private route: ActivatedRoute,
    private router: Router, private folderService:FolderService,
    private authenticationService: AuthenticationService) { }
loginErrorMessage=null;
  ngOnInit() {
     // reset login status
     this.authenticationService.logout();
     localStorage.removeItem("currentUser");
      // get return url from route parameters or default to '/'
      this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

      this.fetchLoginPreferences(true);
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

}
