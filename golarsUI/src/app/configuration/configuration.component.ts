import { Component, OnInit } from '@angular/core';
import { ImportFieldValues } from '../import/import.mapping';
import { FolderService } from '../services/folder.service';
import { CommonService } from '../services/common.service';

@Component({
  selector: 'golars-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.css']
})
export class ConfigurationComponent implements OnInit {

  constructor(private commonService: CommonService, private folderService: FolderService) { }
  docTypeTableCols = [];
  stateProgramCols = [];
  docTypeTableValues = [];
  scopeOfWorkTableValues = [];
  showSuccessMessage = false;
  successMessage = null;
  statePrograTableValues = []
  dropDownValue = "State Program";
  dropdownOptions;
  textAreaTitle = "Add Picklist for State Program";
  pickListValue = "";
  stateProgram = ImportFieldValues.stateProgramMapping;
  stateProgramMappingForDocumentType = ImportFieldValues.stateProgramMappingForDocumentType;
  stateProgramMappingForScopeOfWork = ImportFieldValues.stateProgramMappingForScopeOfWork;
  stateProgramproeprties = [];
  preferencesData = [];
  loginContentURL;
  ngOnInit() {

    this.generateConfigurationData();
  }
  generateConfigurationData() {
    this.docTypeTableCols = [];
    this.statePrograTableValues = [];
    this.stateProgramCols = [];
    this.docTypeTableValues = [];
    this.scopeOfWorkTableValues = []
    this.stateProgram = this.commonService.getStateProgramPreferences();
    this.stateProgramMappingForDocumentType = this.commonService.getDocumentTypePreferences();
    this.stateProgramMappingForScopeOfWork = this.commonService.getScopeOfWorkPreferences();
    if (this.commonService.getScopeOfWorkPreferences() != null)
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
    this.modifyModelValue(tdData)
  }
  modifyModelValue(tdData) {
    if (this.dropDownValue == 'State Program') {
      for (var i = 0; i < this.stateProgram.length; i++)
        if (this.stateProgram[i].label == tdData.label)
          this.stateProgram[i].enable = tdData.enable;
    }
  }
  docTypeTdClickced(tdData, trData, colheader, $event) {
    if (this.dropDownValue == 'Document Type') {
    var index = this.docTypeTableValues.indexOf(trData);
    for (var i = 0; i < this.stateProgramMappingForDocumentType.length; i++) {
      if (this.stateProgramMappingForDocumentType[i].label == colheader) {
        this.stateProgramMappingForDocumentType[i].properties[index].enable = !tdData.enable;
        tdData.enable = !tdData.enable;

      }
    }
  }else  if (this.dropDownValue == 'Scope of Work') {
    var index = this.scopeOfWorkTableValues.indexOf(trData);
    for (var i = 0; i < this.stateProgramMappingForScopeOfWork.length; i++) {
      if (this.stateProgramMappingForScopeOfWork[i].label == colheader) {
        this.stateProgramMappingForScopeOfWork[i].properties[index].enable = !tdData.enable;
        tdData.enable = !tdData.enable;

      }
  }
}


    console.log(this.docTypeTableValues.indexOf(trData))

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
    this.  showSuccessMessage = false;
  this.successMessage = null;
    this.generateConfigurationData()
  }
  addToList() {
    var arr = this.pickListValue.split("\n");
    for (var i = 0; i < arr.length; i++) {
      if (this.dropDownValue == 'State Program') {
        if (!this.notPresentInStateProgramTableData(this.statePrograTableValues, arr[i])) {
          this.statePrograTableValues.push({});
          this.statePrograTableValues[this.statePrograTableValues.length - 1][this.stateProgramCols[0].field] = { label: arr[i], enable: false };
          this.stateProgram.push({ label: arr[i], value: arr[i], enable: false })
          // this.stateProgram[this.stateProgram.length][this.stateProgramCols[0].field] = { label: arr[i], enable: false };
        } else {
          this.stateProgram[this.stateProgram.length - 1].enable = true;
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
        this.scopeOfWorkTableValues = [];
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
  resetProperties(properties) {
    for (var i = 0; i < properties.length; i++) {
      properties[i].enable = false;
    }
    return properties
  }
  loadDocTypeTableData() {
    for (var colIndex = 0; colIndex < this.docTypeTableCols.length; colIndex++) {
      // var col = this.docTypeTableCols[colIndex];
      //   if (this.stateProgramMappingForDocumentType[colIndex] == null || this.stateProgramMappingForDocumentType[colIndex].label == null){
      //   this.stateProgramproeprties = this.getStateProgramMap(this.stateProgramMappingForDocumentType[0].label);
      //  var properties = this.resetProperties(this.stateProgramproeprties);
      //   this.stateProgramMappingForDocumentType.push({label:col.header,properties})
      //   for (var i = 0; i < this.stateProgramproeprties.length; i++) {
      //     if (colIndex == 0)
      //       this.docTypeTableValues.push({});
      //     var prop = this.stateProgramproeprties[i];
      //     this.docTypeTableValues[i][this.docTypeTableCols[colIndex].field] = { label: prop.label, enable: false };

      //     //  this.stateProgramMappingForDocumentType[i][this.docTypeTableCols[colIndex].field] = { label: prop.label, enable: false };
      //   }
      // }
      //   else{
      this.stateProgramproeprties = this.getStateProgramMap(this.stateProgramMappingForDocumentType[colIndex].label)

      for (var i = 0; i < this.stateProgramproeprties.length; i++) {
        if (colIndex == 0)
          this.docTypeTableValues.push({});
        var prop = this.stateProgramproeprties[i];
        this.docTypeTableValues[i][this.docTypeTableCols[colIndex].field] = { label: prop.label, enable: prop.enable };
      }
      // }
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

  saveSateProgramPreferences() {
    this.preferencesData = [];


    /*doc type start*/
    var docTypeArray = []
    for (var i = 0; i < this.stateProgram.length; i++) {
      for (var j = 0; j < this.stateProgramMappingForDocumentType.length; j++) {
        var found = false;
        if (this.stateProgramMappingForDocumentType[j].label == this.stateProgram[i].label) {
          docTypeArray.push(this.stateProgramMappingForDocumentType[j]);
          found = true;
          break;
        }

      }
      if (!found) {
        this.stateProgramproeprties = this.getStateProgramMap(this.stateProgramMappingForDocumentType[0].label);
        var properties = this.resetProperties(this.stateProgramproeprties);
        docTypeArray.push({ label: this.stateProgram[i].label, properties });
      }
    }
    this.stateProgramMappingForDocumentType = docTypeArray;
    /* doc type end */

    var scopeOfWorkArray = []
    for (var i = 0; i < this.stateProgram.length; i++) {
      for (var j = 0; j < this.stateProgramMappingForScopeOfWork.length; j++) {
        var found = false;
        if (this.stateProgramMappingForScopeOfWork[j].label == this.stateProgram[i].label) {
          scopeOfWorkArray.push(this.stateProgramMappingForScopeOfWork[j]);
          found = true;
          break;
        }

      }
      if (!found) {
        this.stateProgramproeprties = this.getScopeOfWorkMap(this.stateProgramMappingForScopeOfWork[0].label);
        var properties = this.resetProperties(this.stateProgramproeprties);
        scopeOfWorkArray.push({ label: this.stateProgram[i].label, properties });
      }
    }
    this.stateProgramMappingForScopeOfWork = scopeOfWorkArray;

    this.preferencesData.push({ key: "stateProgramPreferences", value: JSON.stringify(this.stateProgram) });
    this.preferencesData.push({ key: "docTypePreferences", value: JSON.stringify(this.stateProgramMappingForDocumentType) });
    this.preferencesData.push({ key: "scopeOfWorkPreferences", value: JSON.stringify(this.stateProgramMappingForScopeOfWork) });

    console.log(this.preferencesData);
    this.folderService.saveTablePreferences(this.preferencesData)
      .subscribe(
        data => {
          this.showSuccessMessage = true;
          this.successMessage = "Configuration saved successfully";
          this.commonService.updatePreferences(data);


        },
        error => {
          console.log(error);
        });
  }
  saveDocumentTypePreferences() {
    this.preferencesData = [];

    this.preferencesData.push({ key: "docTypePreferences", value: JSON.stringify(this.stateProgramMappingForDocumentType) });
    console.log(this.preferencesData);
    this.folderService.saveTablePreferences(this.preferencesData)
      .subscribe(
        data => {
          this.showSuccessMessage = true;
          this.successMessage = "Configuration saved successfully";
          this.commonService.updatePreferences(data);
        },
        error => {
          console.log(error);
        });
  }
  saveScopeOfWorkPreferences() {
    this.preferencesData = [];

    this.preferencesData.push({ key: "scopeOfWorkPreferences", value: JSON.stringify(this.stateProgramMappingForScopeOfWork) });
    console.log(this.preferencesData);
    this.folderService.saveTablePreferences(this.preferencesData)
      .subscribe(
        data => {
          this.showSuccessMessage = true;
          this.successMessage = "Configuration saved successfully";
          this.commonService.updatePreferences(data);
        },
        error => {
          console.log(error);
        });
  }
  deleteRowData(trData) {
    if (this.dropDownValue == 'State Program') {
      console.log(this.statePrograTableValues.indexOf(trData))
      var index = this.statePrograTableValues.indexOf(trData);
      this.statePrograTableValues.splice(index, 1)
      this.stateProgram.splice(index, 1)
    }else    if (this.dropDownValue == 'Document Type') {
      console.log(this.docTypeTableValues.indexOf(trData))
      var index = this.docTypeTableValues.indexOf(trData);
      this.docTypeTableValues.splice(index, 1)
      for (var i = 0; i < this.stateProgramMappingForDocumentType.length; i++)
        this.stateProgramMappingForDocumentType[i].properties.splice(index, 1);
    }else  if (this.dropDownValue == 'Scope of Work') {
      console.log(this.scopeOfWorkTableValues.indexOf(trData))
      var index = this.scopeOfWorkTableValues.indexOf(trData);
      this.scopeOfWorkTableValues.splice(index, 1)
      for (var i = 0; i < this.stateProgramMappingForScopeOfWork.length; i++)
        this.stateProgramMappingForScopeOfWork[i].properties.splice(index, 1);
    }

  }
  saveLoginContentURL(){
    this.preferencesData=[];
    this.preferencesData.push({ key: "loginContentURL", value: JSON.stringify(this.loginContentURL) });
    this.folderService.saveTablePreferences(this.preferencesData)
    .subscribe(
      data => {
        this.showSuccessMessage = true;
        this.successMessage = "Login Content URL saved successfully";
        this.commonService.updatePreferences(data);


      },
      error => {
        console.log(error);
      });
  }
}


