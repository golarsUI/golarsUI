import { Component, OnInit, ViewChild } from '@angular/core';
import { UserService } from '../services/user.service';
import { FormGroup, NgForm } from '@angular/forms';

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
  disableRegisterButton=false;
  @ViewChild('f') formElem: NgForm;
  ngOnInit() {
  }
  register() {
    this.resetSuccessAndFailureMessages();
    this.model.newlyCreated = true;
    this.userService.registerUser(this.model)
      .subscribe(
        result => {
          if (result == true) {
            
            // this.model=[];
            this.showSuccessMessage = true;
            this.successMessage = "User Created Successfully.";
            this.disableRegisterButton = true;
          //   this.model=[];
          //   this.formElem.form.reset();
          //   Object.keys(this.formElem.controls).forEach(control => {
          //     this.formElem.controls[control].setErrors(null);
          //     this.formElem.controls[control].markAsPristine();
             
          // });
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
    this.disableRegisterButton=false;
  }
}
