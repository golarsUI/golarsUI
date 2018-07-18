import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'golars-resetpassword',
  templateUrl: './resetpassword.component.html',
  styleUrls: ['./resetpassword.component.css']
})
export class ResetpasswordComponent implements OnInit {
  model: any = {};
  showSuccessMessage=false;
  showresetErrorMessage = false;
  userName;
  @ViewChild('f') formGroup: FormGroup;  
  constructor(private route: ActivatedRoute,
     private router: Router,private authenticationService: AuthenticationService,private userService: UserService) { }
  passwordDoesnotmatch=false; 
  ngOnInit() {
    this.route
      .queryParams
      .subscribe(params => {
        console.log(params);
this.userName = params.username;
        // this.jobObject = JSON.parse(params.jobObject);
      });
  }
  logout(){
    this.authenticationService.logout();
    this.router.navigate(['/login']);

  }
  changePassword(){
    this.model.userName = this.userName;
    this.model.reset = true;
  if(this.model != null && this.model.newPassword!=null &&  this.model.confirmPassword != null  && this.model.newPassword !== this.model.confirmPassword){
  this.formGroup.controls['confirmPassword'].setErrors({'mismatch': true});
  this.passwordDoesnotmatch=true;
  }else{
this.userService.changePassword(this.model) .subscribe(
  data => {
     // console.log(message)
     if(data === true){
  this.showSuccessMessage=true;
  this.authenticationService.logout();
  
     console.log(data)
    }else{
     this.showresetErrorMessage =true; 
    }
  },
  error => {
    console.log(error)
  });
  }
}
  

}
