import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map'
import { URLConstants } from '../constants/urlconstants';
import { FolderService } from './folder.service';
import { CommonService } from './common.service';
import { GolarsConstants } from '../constants/golarsconstants';

@Injectable()
export class AuthenticationService {

    constructor(private http: HttpClient,private folderService: FolderService,private commonService:CommonService) { }

    login(username: string, password: string) {
        return this.http.post<any>(URLConstants.LOGIN_URL, { username: username, password: password })
            .map(user => {
                // login successful if there's a token in the response
                if (user && user.token) {
                    // store user details and token in local storage to keep user logged in between page refreshes
                    localStorage.setItem('currentUser', JSON.stringify(user));
                    localStorage.setItem('username', user.username);
                    localStorage.setItem('admin', user.admin);
                    this.fetchUserPreferences(user.admin)
                }

                return user;
            });
    }

    logout() {
        // remove user from local storage to log user out
        // localStorage.removeItem('currentUser');
        // localStorage.removeItem('username');
        // localStorage.removeItem('admin');
        localStorage.clear();
    }
    fetchUserPreferences(admin){
        this.folderService.fetchTablePreferences(admin)
        .subscribe(
            data => {
                this.commonService.updatePreferences(data);
                      },
            error => {
                console.log(error);
            });
    }
    forgotPassword(forgotemail,resetPasswordLink){
        return this.http.post<any>(URLConstants.FORGOT_PASSWORD_URL, forgotemail+"&&@@#@"+ resetPasswordLink)
        .map(message => {
           
            return message;
        });  
    }
   
}