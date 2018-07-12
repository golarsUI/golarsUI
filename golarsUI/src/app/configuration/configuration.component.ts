import { Component, OnInit } from '@angular/core';
import { ImportFieldValues } from '../import/import.mapping';

@Component({
  selector: 'golars-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.css']
})
export class ConfigurationComponent implements OnInit {

  constructor() { }
  docTypeTableCols = [];
  stateProgramCols = [];
  docTypeTableValues = [];
  scopeOfWorkTableValues = [];

  statePrograTableValues = []
  dropDownValue = "State Program";
  dropdownOptions;
  textAreaTitle = "Add Picklist for State Program";
  pickListValue = "";
  stateProgram = ImportFieldValues.stateProgramMapping;
  stateProgramMappingForDocumentType = ImportFieldValues.stateProgramMappingForDocumentType;
  stateProgramMappingForScopeOfWork = ImportFieldValues.stateProgramMappingForScopeOfWork;
  stateProgramproeprties = [];
  ngOnInit() {

    for (var i = 0; i < this.stateProgram.length; i++) {
      this.docTypeTableCols.push({ field: this.stateProgram[i].label, header: this.stateProgram[i].value })
    }

    this.stateProgramCols.push({ field: "State Program", header: "State Program" })


    this.loadDocTypeTableData();

    // state program table
    for (var colIndex = 0; colIndex < this.stateProgramCols.length; colIndex++) {
      for (var i = 0; i < this.stateProgram.length; i++) {

        this.statePrograTableValues.push({});
        this.statePrograTableValues[i][this.stateProgramCols[colIndex].field] = { label: this.stateProgram[i].label, enable: this.stateProgram[i].enable };
      }
    }

    this.loadScopeOfWorkTableData();


    this.dropdownOptions = [
      { label: 'State Program', value: 'State Program' },
      { label: 'Document Type', value: 'Document Type' },
      { label: 'Scope of Work', value: 'Scope of Work' },
    ];
  }
  getStateProgramMap(label) {
    for (var i = 0; i < this.stateProgramMappingForDocumentType.length; i++)
      if (this.stateProgramMappingForDocumentType[i].label == label)
        return this.stateProgramMappingForDocumentType[i].properties;

  }
  getScopeOfWorkMap(label) {
    for (var i = 0; i < this.stateProgramMappingForScopeOfWork.length; i++)
      if (this.stateProgramMappingForScopeOfWork[i].label == label)
        return this.stateProgramMappingForScopeOfWork[i].properties;

  }

  tdClickced(tdData, $event) {
    console.log(tdData, $event)
    tdData.enable = !tdData.enable;
  }

  getTableTooltip(isEnable) {
    if (isEnable)
      return "Click to Disable";
    return "Click to Enable";
  }
  changeTextAreaHeading($event) {
    this.textAreaTitle = "Add Picklist for " + $event.value;
    this.pickListValue = "";
    console.log($event)
  }
  addToList() {
    var arr = this.pickListValue.split("\n");
    for (var i = 0; i < arr.length; i++) {
      if (this.dropDownValue == 'State Program') {
        if (!this.notPresentInStateProgramTableData(this.statePrograTableValues, arr[i])) {
          this.statePrograTableValues.push({});
          this.statePrograTableValues[this.statePrograTableValues.length - 1][this.stateProgramCols[0].field] = { label: arr[i], enable: false };
        }
      } else if (this.dropDownValue == 'Document Type') {
        this.docTypeTableValues = [];
        for (var colIndex = 0; colIndex < this.docTypeTableCols.length; colIndex++) {
          var col = this.docTypeTableCols[colIndex];
          if (!this.notPresentInTableData(this.stateProgramMappingForDocumentType[colIndex].properties, arr[i]))
            this.stateProgramMappingForDocumentType[colIndex].properties.push({ label: arr[i], value: arr[i], enable: false })
        }
        this.loadDocTypeTableData();
      } else if (this.dropDownValue == 'Scope of Work') {
        this.docTypeTableValues = [];
        for (var colIndex = 0; colIndex < this.docTypeTableCols.length; colIndex++) {
          var col = this.docTypeTableCols[colIndex];
          if (!this.notPresentInTableData(this.stateProgramMappingForScopeOfWork[colIndex].properties, arr[i]))
            this.stateProgramMappingForScopeOfWork[colIndex].properties.push({ label: arr[i], value: arr[i], enable: false })
        }
        this.loadScopeOfWorkTableData();
      }
    }
    console.log(this.dropDownValue)

  }
  loadDocTypeTableData() {
    for (var colIndex = 0; colIndex < this.docTypeTableCols.length; colIndex++) {
      var col = this.docTypeTableCols[colIndex];
      this.stateProgramproeprties = this.getStateProgramMap(this.stateProgramMappingForDocumentType[colIndex].label)

      for (var i = 0; i < this.stateProgramproeprties.length; i++) {
        if (colIndex == 0)
          this.docTypeTableValues.push({});
        var prop = this.stateProgramproeprties[i];
        this.docTypeTableValues[i][this.docTypeTableCols[colIndex].field] = { label: prop.label, enable: prop.enable };
      }
    }
  }
  loadScopeOfWorkTableData() {
    for (var colIndex = 0; colIndex < this.docTypeTableCols.length; colIndex++) {
      var col = this.docTypeTableCols[colIndex];
      this.stateProgramproeprties = this.getScopeOfWorkMap(this.stateProgramMappingForScopeOfWork[colIndex].label)

      for (var i = 0; i < this.stateProgramproeprties.length; i++) {
        if (colIndex == 0)
          this.scopeOfWorkTableValues.push({});
        var prop = this.stateProgramproeprties[i];
        this.scopeOfWorkTableValues[i][this.docTypeTableCols[colIndex].field] = { label: prop.label, enable: prop.enable };
      }
    }
  }
  notPresentInTableData(properties, searchString) {
    for (var i = 0; i < properties.length; i++) {
      if (properties[i].label == searchString) {
        properties[i].enable = true;
        return true;
      }

    }
    return false;
  }
  notPresentInStateProgramTableData(data, searchString) {
    for (var i = 0; i < data.length; i++) {
      if (data[i][this.stateProgramCols[0].field].label == searchString) {
        data[i][this.stateProgramCols[0].field].enable = true;
        return true;
      }

    }
    return false;


  }
}


