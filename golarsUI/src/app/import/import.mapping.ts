export class ImportFieldValues {

  static docTypeMapping = [
  //  {label:'Select Document Type', value:null,key:" IN - Brownfields ,  IN - ELTF ,  IN - LUST , IN - State Cleanup,IN - UST, IN - VRP "},
   { label: '329 IAC 9-3-1 Records Request', value:'329 IAC 9-3-1 Records Request', key:" IN - UST"},
   {label: 'COFA Approved',value:'COFA Approved', key:" IN - UST"},  
   {label: 'Community Relations',value:'Community Relations' , key:"IN - Brownfields, IN - State Cleanup, IN - VRP " },
   {label: 'Corrective Action',value:'Corrective Action' , key:" IN - UST,IN - Brownfields, IN - ELTF, IN - LUST, IN - State Cleanup  "},
   {label: 'Correspondence',value:'Correspondence' , key:" IN - UST,IN - Brownfields, IN - ELTF, IN - LUST , IN - State Cleanup , IN - VRP "},
   {label: 'Cost Recovery',value:'Cost Recovery' , key:"IN - Brownfields, IN - State Cleanup , IN - VRP"},
   {label: 'ELTF Claim',value:'ELTF Claim', key:" IN - ELTF"},
   {label: 'Email Correspondence',value:'Email Correspondence' , key:" IN - UST"},
   {label: 'Emergency Response',value:'Emergency Response', key:"IN - Brownfields, IN - LUST, IN - State Cleanup  "},
   {label: 'Enforcement',value:'Enforcement' , key:" IN - UST,IN - Brownfields, IN - LUST , IN - State Cleanup , IN - VRP"},
   {label: 'Financial Responsibility',value:'Financial Responsibility', key:" IN - UST,"},
   {label: 'Inspection',value:'Inspection' , key:" IN - UST, IN - State Cleanup "},
   {label: 'Institutional Control',value:'Institutional Control', key:"IN - Brownfields, IN - LUST, IN - State Cleanup , IN - VRP "},
   {label: 'LUST',value:'LUST', key:"LUST"},
   {label: 'Notification Form',value:'Notification Form' , key:" IN - UST"},
   {label: 'Notification Form Denied',value:'Notification Form Denied' , key:" IN - UST"},
   {label: 'Other',value:'Other' , key:" IN - UST, IN - State Cleanup "},
   {label: 'Quarterly Monitoring',value:'Quarterly Monitoring' ,key:"IN - Brownfields, IN - LUST, IN - State Cleanup , IN - VRP "},
   {label: 'Remediation',value:'Remediation' , key:"IN - VRP"},
   {label: 'Return to Compliance',value:'Return to Compliance' , key:" IN - UST,"},
   {label: 'Screening/Assessment',value:'Screening/Assessment',key:"IN - Brownfields, IN - LUST , IN - State Cleanup , IN - VRP "},
   {label: 'Site Characterization - FSI',value:'Site Characterization - FSI', key:"IN - Brownfields, IN - LUST, IN - State Cleanup  "},
   {label: 'Site Characterization – FSI',value:'Site Characterization – FSI' , key: "IN - VRP"},
   {label: 'Site Characterization - ISC',value:'Site Characterization - ISC',key:"IN - Brownfields, IN - LUST , IN - State Cleanup "},
   {label: 'Site Characterization – ISI',value:'Site Characterization – ISI' , key:"IN - VRP"},
   {label: 'Site Characterization - LSI',value:'Site Characterization - LSI', key:"IN - Brownfields, IN - LUST, IN - State Cleanup , IN - VRP "},
   {label: 'Site Closure',value:'Site Closure' , key:'IN - Brownfields, IN - LUST, IN - State Cleanup , IN - VRP '},
   {label: 'Site Decision',value:'Site Decision', key:"IN - State Cleanup "},
   {label: 'Technical Review',value:'Technical Review',key:"IN - Brownfields, IN - State Cleanup "},
   {label: 'UST Closure',value:'UST Closure', key:" IN - LUST , IN - State Cleanup "},
   {label: 'UST Closure Report',value:'UST Closure Report' , key:" IN - UST, IN - LUST "},
   {label: 'UST Removal Approval',value:'UST Removal Approval' , key:" IN - UST,"},
   {label: 'UST Removal Request',value:'UST Removal Request' , key:" IN - UST,"},
   {label: 'Violation',value:'Violation' , key:" IN - UST,IN - Brownfields, IN - LUST , IN - State Cleanup "},
   {label: 'VRP Application',value:'VRP Application', key:"IN - VRP"},
  ];

  static scopeOfWorkMapping = [
   {label: 'ATG Functionality',value:'ATG Functionality',key:" IN - UST "},
    {label: 'Cathodic Protection (Impressed) Test',value:'Cathodic Protection (Impressed) Test' ,key:" IN - UST "},
    {label: 'Cathodic Protection (Sacrificial) Test',value:'Cathodic Protection (Sacrificial) Test',key:" IN - UST "},
    {label: 'Compliance Management',value:'Compliance Management',key:" IN - UST "},
    {label: 'Drum Disposal',value:'Drum Disposal',key:" IN - UST "},
    {label: 'E-Stop and Interstitial Sensor Tests',value:'E-Stop and Interstitial Sensor Tests',key:" IN - UST "},
    {label: 'Helium Leak Testing',value:'Helium Leak Testing',key:" IN - UST "},
    {label: 'Impact Valve',value:'Impact Valve',key:" IN - UST "},
    {label: 'Internal Tank Lining Inspection',value:'Internal Tank Lining Inspection',key:" IN - UST "},
    {label: 'Interstitial Tank Pressure Testing',value:'Interstitial Tank Pressure Testing',key:" IN - UST "},
    {label: 'Line Tightness Testing',value:'Line Tightness Testing',key:" IN - UST "},
    {label: 'Notification Form',value:'Notification Form',key:" IN - UST "},
    {label: 'Operator Training',value:'Operator Training',key:" IN - UST "},
    {label: 'State Cleanup',value:'State Cleanup',key:" IN - UST "},
    {label: 'Sump Sensor',value:'Sump Sensor',key:" IN - UST "},
    {label: 'Tank Closure',value:'Tank Closure',key:" IN - UST "},
    {label: 'Tank Tightness Testing',value:'Tank Tightness Testing',key:" IN - UST "},
    {label: 'Voluntary Remediation Program (VRP)',value:'Voluntary Remediation Program (VRP)',key:" IN - UST "},
    {label: 'Active',value:'Active',key:" IN - LUST ,  IN - State Cleanup ,  IN - VRP  "},
    {label: 'Compliant',value:'Compliant',key:" IN - LUST ,  IN - State Cleanup ,  IN - VRP  "},
    {label: 'Deactivated (No Release)',value:'Deactivated (No Release)',key:" IN - LUST ,  IN - State Cleanup ,  IN - VRP  "},
    {label: 'Discontinued (Active)',value:'Discontinued (Active)',key:" IN - LUST ,  IN - State Cleanup ,  IN - VRP  "},
    {label: 'NFA',value:'NFA',key:" IN - LUST ,  IN - State Cleanup ,  IN - VRP  "},
    {label: 'No Paper File (Active)',value:'No Paper File (Active)',key:" IN - LUST ,  IN - State Cleanup ,  IN - VRP  "},
    {label: 'Reffered to Another IDEM Program',value:'Reffered to Another IDEM Program',key:" IN - LUST ,  IN - State Cleanup ,  IN - VRP  "},

  ] 
  
  static stateProgramMapping = [
    // {label:'Select State Program', value:null},
    {label: 'IN - Brownfields',value:'IN - Brownfields'},
    {label: 'IN - ELTF',value:'IN - ELTF'},
    {label: 'IN - LUST',value:'IN - LUST'},
    {label: 'IN - State Cleanup',value:'IN - State Cleanup'},
    {label: 'IN - UST',value:'IN - UST'},
    {label: 'IN - VRP',value:'IN - VRP'},
  ]
  
    

  
  

}
