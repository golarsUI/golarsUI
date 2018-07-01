import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';

@Component({
  selector: 'golars-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  users;
  constructor(private userService: UserService) { }

  ngOnInit() {
    this.userService.getAllUsers()
        .subscribe(
            data => {
              // data.fo
              this.users = data; 
            },
            error => {
                console.log(error);
            });
      
      }
      editUser(user){
        console.log("editUser ",user)
      }
      deleteUser(user){
        console.log("deleteUser ",user)
      }
  }


