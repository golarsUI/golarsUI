import { Injectable } from '@angular/core';

import { URLConstants } from '../constants/urlconstants';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class UserService {

  constructor(private http: HttpClient) { }

  getAllUsers() {
      return this.http.get<any>(URLConstants.USERS_URL)
          .map(users => {
              
              return users;
          });
  }
}
