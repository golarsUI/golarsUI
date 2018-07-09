import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
declare var $: any;

@Component({
  selector: 'golars-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  users;
  constructor(private userService: UserService) { }
  selectedUser;
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
  editUser(user) {
    console.log("editUser ", user)
  }
  deleteUser(user) {
    this.selectedUser = user;
    $('#user_select_delete_model').modal('show');

  }
  deleteUserOnConfirmation() {
    this.userService.deleteUser(this.selectedUser).subscribe(
      data => {

        // data.fo
        this.users = data;
        $('#user_select_delete_model').modal('hide');

      },
      error => {
        console.log(error);
      });
  }

}


