import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';
import { CommonService } from '../services/common.service';
import { NgForm, FormGroup } from '@angular/forms';
import { FileUpload } from 'primeng/primeng';
import { UserService } from '../services/user.service';

@Component({
  selector: 'golars-changepassword',
  templateUrl: './changepassword.component.html',
  styleUrls: ['./changepassword.component.css']
})

export class ChangepasswordComponent implements OnInit {
  model: any = {};
  showSuccessMessage=false;
  @ViewChild('f') formGroup: FormGroup;  
  constructor(private router: Router,private authenticationService: AuthenticationService,private userService: UserService) { }
  passwordDoesnotmatch=false; 
  ngOnInit() {
  }
  logout(){
    this.authenticationService.logout();
    this.router.navigate(['/login']);

  }
  changePassword(){
    this.model.userName = localStorage.getItem('username');
  if(this.model != null && this.model.newPassword!=null &&  this.model.confirmPassword != null  && this.model.newPassword !== this.model.confirmPassword){
  this.formGroup.controls['confirmPassword'].setErrors({'mismatch': true});
  this.passwordDoesnotmatch=true;
  }else{
this.userService.changePassword(this.model) .subscribe(
  data => {
     // console.log(message)
this.showSuccessMessage=true;
this.authenticationService.logout();
     console.log(data)
  },
  error => {
    console.log(error)
  });
  }
}
}
