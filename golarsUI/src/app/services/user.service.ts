import { Injectable } from '@angular/core';

import { URLConstants } from '../constants/urlconstants';
import { HttpClient, HttpParams } from '@angular/common/http';
import { GolarsConstants } from '../constants/golarsconstants';

@Injectable()
export class UserService {

  constructor(private http: HttpClient) { }

  getAllUsers() {
      return this.http.get<any>(URLConstants.USERS_URL)
          .map(users => {
              
              return users;
          });
  }
  deleteUser(user){
    return this.http.delete<any>(URLConstants.USERS_URL,this.getDeleteUserOptions(user.username))
    .map(users => {
        
        return users;
    });
  }
  registerUser(modal){
    return this.http.post<any>(URLConstants.USERS_URL, { firstName: modal.firstName, lastName: modal.lastName, emailAddress:modal.email,
        admin: modal.admin,username:modal.username,password:modal.password,newlyCreated:modal.newlyCreated,permissonFolderID:modal.permissonFolderID })
    .map(user => {
        // Registration response 
        return user;
    });

  }
  changePassword(modal){
    return this.http.post<any>(URLConstants.USER_CHANGE_PASSWORD_URL, { username: modal.userName, password: modal.currentPassword, updatedPassword:modal.confirmPassword,reset:modal.reset })
    .map(successMessage => {
        // Change password response
        return successMessage;
    });

  }
  private getDeleteUserOptions(username) {
    return {
      params: new HttpParams().set(GolarsConstants.USERNAME,username)
    };
  }
}
