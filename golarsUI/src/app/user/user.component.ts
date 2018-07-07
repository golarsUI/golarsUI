import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';

@Component({
  selector: 'golars-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  model: any = {};
  constructor(private userService: UserService) { }
  showSuccessMessage = false;
  showFailureMessage = false;
  successMessage = null;
  failureMessage = null;
  ngOnInit() {
  }
  register() {
    this.resetSuccessAndFailureMessages();
    this.model.newlyCreated = true;
    this.userService.registerUser(this.model)
      .subscribe(
        result => {
          if (result == true) {
            this.showSuccessMessage = true;
            this.successMessage = "User Created Successfully.";
          } else {
            this.showFailureMessage = true;
            this.failureMessage = "User Already Exists.";
          }
          console.log(result)
        },
        error => {
          console.log(error)
        });
  }
  resetSuccessAndFailureMessages() {
    this.showSuccessMessage = false;
    this.showFailureMessage = false;
    this.successMessage = null;
    this.failureMessage = null;
  }
}
