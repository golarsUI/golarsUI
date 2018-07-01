import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'golars-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  area = {
    left: 20,
    center: 50,
    right: 30,
    leftVisible:true,
    centerVisible:true,
    rightVisible:true,
    useTransition: true,
}
  constructor() { }

  ngOnInit() {
    
               
  }

}
