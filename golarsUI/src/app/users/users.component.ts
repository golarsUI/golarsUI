import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { CommonService } from '../services/common.service';
declare var $: any;

@Component({
  selector: 'golars-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  users;
  constructor( private router: Router,private commonService: CommonService,private userService: UserService) { }
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
   localStorage.setItem("editUser",JSON.stringify(user));
   this.router.navigate(['newuser']);

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


