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
  showSuccessMessage=false;
  successMessage=null;
  ngOnInit() {
  }
  register() {

    this.userService.registerUser(this.model)
        .subscribe(
            data => {
               // console.log(message)
          this.showSuccessMessage=true;
          this.successMessage = "User Created Successfully !!";
               console.log(data)
            },
            error => {
              console.log(error)
            });
}

}
