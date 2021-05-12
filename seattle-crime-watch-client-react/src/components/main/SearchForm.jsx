import React, { Component } from 'react';
import allOffense from '../../static/Offense.json';
import allOffenseParent from '../../static/OffenseParent.json';
import allOffenseCategory from '../../static/OffenseCategory.json';
import allMcpp from '../../static/mcpp.json';
import allZipcode from '../../static/zipcode.json';

import Autocomplete from '@material-ui/lab/Autocomplete';
import TextField from '@material-ui/core/TextField';
import DateFnsUtils from '@date-io/date-fns'; 
import {MuiPickersUtilsProvider, KeyboardDatePicker} from '@material-ui/pickers';
import Grid from '@material-ui/core/Grid';
import { ThemeProvider, createMuiTheme } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import MyChart from '../chart/MyChart'
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import ArrowForwardIcon from '@material-ui/icons/ArrowForward';
import IconButton from '@material-ui/core/IconButton';

import './css/SearchForm.css';
import pic from "../../img/logo.png"
const axios = require('axios').default;

const theme = createMuiTheme({
  typography: {
    fontFamily: [
      'Special Elite'
    ].join(',')
  }
});

function formatDate(date) {
  var d = new Date(date),
      month = '' + (d.getMonth() + 1),
      day = '' + d.getDate(),
      year = d.getFullYear();

  if (month.length < 2) 
      month = '0' + month;
  if (day.length < 2) 
      day = '0' + day;

  return [year, month, day].join('-');
}

class SearchForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      offenseType: "",
      offenseData: [],
      allOffenseData: [],
      locationType: "",
      locationData: "",
      allLocationData: [],
      startDate: null,
      endDate: null,
      timeError: "",
      timeError1: "",
      errorMessage: "",
      crimes: [],
      series: [],
      categories : [],
      displayTable: true
    };  
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleOffenseTypeChange = this.handleOffenseTypeChange.bind(this);
    this.handleOffenseDataChange = this.handleOffenseDataChange.bind(this);
    this.selectOffenseType = this.selectOffenseType.bind(this);
    this.handleLocationTypeChange = this.handleLocationTypeChange.bind(this);
    this.handleLocationDataChange = this.handleLocationDataChange.bind(this);
    this.selectLocationType = this.selectLocationType.bind(this);
    this.handleStartDateChange = this.handleStartDateChange.bind(this);
    this.handleEndDateChange = this.handleEndDateChange.bind(this);
    this.clearContent = this.clearContent.bind(this);
    this.helper=this.helper.bind(this);
    this.changeDisplay=this.changeDisplay.bind(this);
  }

  handleOffenseTypeChange(event) {
    this.setState({offenseType: event.target.value});
    // After changing type, the former offense data must be cleared
    this.setState({offenseData: []});
    this.selectOffenseType(event.target.value);
  }

  selectOffenseType = (type) => {
    if (type === 'offenseName') {
        this.setState({ allOffenseData: allOffense.map(offense => offense.OffenseName)});
    } else if (type === 'offenseParent') {
        this.setState({ allOffenseData: allOffenseParent.map(offense => offense.OffenseParentName)});
    } else {
        this.setState({ allOffenseData: allOffenseCategory.map(offense => offense.OffenseCategory)});
    }
  };

  handleOffenseDataChange(event) {
    this.setState({offenseData: event.target.value});
  }

  handleLocationTypeChange(event) {
    this.setState({locationType: event.target.value});
    // After changing type, the former location data must be cleared
    this.setState({locationData: null});
    this.selectLocationType(event.target.value);
  }

  selectLocationType = (type) => {
    if (type === 'zipcode') {
        this.setState({ allLocationData: allZipcode.map(zipcode => zipcode.zipcode)});
        this.setState({locationData: allZipcode[0].zipcode});
    } else {
        this.setState({ allLocationData: allMcpp.map(mcpp => mcpp.MCPPName)});
        this.setState({locationData: allMcpp[0].MCPPName});
    }
  };

  handleLocationDataChange(event) {
    this.setState({locationData: event.target.value});
  }

  handleStartDateChange(date) {
    this.setState({startDate: date, timeError: "", timeError1: ""});
    if (date <= new Date(2008,0,1)) {
      this.setState({timeError: "You can only search data after 2008-01-01! "});
    }
    if (this.state.endDate !== null && date > this.state.endDate) {
      this.setState({timeError1: "The end date must be larger than start date! "});
    }
  }

  handleEndDateChange(date) {
    this.setState({endDate: date, timeError: "", timeError1: ""});
    if (date >= new Date(2020,9,1)) {
      this.setState({timeError: "You can only search data before 2020-10-01! "});
    }
    if (this.state.startDate !== null && date < this.state.startDate) {
      this.setState({timeError1: "The end date must be larger than start date! "});
    }
  }

  clearContent() {
    this.setState({
      offenseType: "",
      offenseData: [],
      allOffenseData: [],
      locationType: "",
      locationData: "",
      allLocationData: [],
      startDate: null,
      endDate: null,
      timeError: "",
      timeError1: "",
      errorMessage: "",
      crimes: [],
      series: [],
      categories : [],
      displayTable: true
    });
  }

  handleSubmit(event) {
    event.preventDefault();
    if (this.state.offenseData.length === 0) {
      this.setState({errorMessage: "Please select at least one offenseName"});
    } else if (this.state.timeError !== "" || this.state.timeError1 !== "") {
      this.setState({errorMessage: "Please fix all errors"});
    } else {
      this.setState({errorMessage: ""});
      axios.post("/api/searchCrimes", {offenseType: this.state.offenseType, offenseName: this.state.offenseData, startDate: formatDate(this.state.startDate), endDate: formatDate(this.state.endDate), zipCode: this.state.zipCode, locationType: this.state.locationType, locationData: this.state.locationData}).then(
        response => {
          this.setState({displayTable: true, crimes: response.data});
          this.helper();
        }
      ).catch(err => console.log(err));
    }
  }

  helper() {
    let x = [];
    let y = [];
    for (let i = 0; i < this.state.crimes[0].length; i++) {
      x.push(this.state.crimes[0][i].crimeTime);
    }
    for (let j = 0; j < this.state.crimes.length; j++) {
      let k = [];
      for (let m = 0; m < this.state.crimes[j].length; m++) {
        k.push(this.state.crimes[j][m].count);
      }
      y.push({name: this.state.crimes[j][0].crimeName, data: k});
    }
    this.setState({categories: x, series: y})
  }

  changeDisplay() {
    this.setState({displayTable: !this.state.displayTable});
  }

  render() {
    return (
      <ThemeProvider theme={theme}>
        <div className="SearchForm">
          <div className='row'><img id='logo' src={pic} alt="Loading..." width="150rem" height='100rem' overflow='hidden'></img></div>
          <form className="form" onSubmit={this.handleSubmit}>
            <Grid container direction="row" justify="center" alignItems="center">
              <Grid item xs={6}>
                <label className="offenseType">
                <Typography variant="h4">Offense Type
                  <select className='selectbox' id="select-offenseType" value={this.state.offenseType} onChange={this.handleOffenseTypeChange} required>
                    <option hidden value="">Choose Offense Type</option>
                    <option value="offenseName">Offense Name</option>
                    <option value="offenseParent">Offense Parent</option>
                    <option value="offenseCategory">Offense Category</option>
                  </select>
                </Typography>
                </label>
              </Grid>
              <Grid item xs={6}>
                <Autocomplete
                  style={{backgroundColor: "white"}}
                  onChange={(event, value) => {
                    this.setState({offenseData: value});
                  }} // prints the selected value
                  multiple
                  id="tags-standard"
                  options={this.state.allOffenseData}
                  getOptionLabel={(option) => option}
                  value={this.state.offenseData}
                  renderInput={(params) => (
                    <TextField
                      {...params}
                      variant="standard"
                      label="Choose OffenseName"
                      placeholder="OffenseName"
                    />
                  )}
                />
              </Grid>
            </Grid>
            <Grid className="date-picker" container direction="row" justify="center" alignItems="center">
              <Grid item xs={6}>
                <MuiPickersUtilsProvider utils={DateFnsUtils}>
                  <KeyboardDatePicker
                    disableToolbar
                    variant="inline"
                    format="MM/dd/yyyy"
                    margin="normal"
                    label="Start Date"
                    value={this.state.startDate}
                    onChange={(newDate) => {this.handleStartDateChange(newDate)}}
                    KeyboardButtonProps={{
                      'aria-label': 'change date',
                    }}
                    className="date-small"
                    required
                  />
                </MuiPickersUtilsProvider>
              </Grid>
              <Grid item xs={6}>
                <MuiPickersUtilsProvider utils={DateFnsUtils}>
                  <KeyboardDatePicker
                    disableToolbar
                    variant="inline"
                    format="MM/dd/yyyy"
                    margin="normal"
                    label="End Date"
                    value={this.state.endDate}
                    onChange={(newDate) => {this.handleEndDateChange(newDate)}}
                    KeyboardButtonProps={{
                      'aria-label': 'change date',
                    }}
                    className="date-small"
                    required
                  />
                </MuiPickersUtilsProvider>
              </Grid>
            </Grid>
            <Typography variant="h5" color="error">{this.state.timeError}</Typography>
            <Typography variant="h5" color="error">{this.state.timeError1}</Typography>
            <Grid container direction="row" justify="center" alignItems="center">
              <Grid item xs={6}>
                <label>
                <Typography variant="h4">Location Type
                  <select className='selectbox' id="select-locationType" value={this.state.locationType} onChange={(newType) => {this.handleLocationTypeChange(newType)}} required>
                    <option hidden value="">Choose Location Type</option>
                    <option value="zipcode">ZipCode</option>
                    <option value="MCPP">MCPP</option>
                  </select>
                </Typography>
                </label>
              </Grid>
              <Grid item xs={6}>
                <label>
                <Typography variant="h4">Location Name
                  <select className='selectbox' id="select-locationData" value={this.state.locationData} onChange={this.handleLocationDataChange} required>
                    <option hidden value="">Choose Location Value</option>
                    {this.state.allLocationData.map((location, index) => {
                      return <option key={index} value={location}>{location}</option>;
                    })}
                  </select>
                </Typography>
                </label>
              </Grid>
            </Grid>
            <Typography variant="h3" color="error">{this.state.errorMessage}</Typography>
            <input id='submit' type="submit" name="action" value="Submit" />
            <input id='submit' type="button" name="action" value="Clear" onClick={this.clearContent} />
          </form>
          {this.state.crimes.length !== 0 && this.state.categories.length !== 0 ? <div className="display">
            {this.state.displayTable ?
              <Grid container justify="center" alignItems="center" direction="row">
                <Grid item xs={1}>
                  <IconButton onClick={this.changeDisplay}>
                    <ArrowBackIcon style={{width: 60, height: 60}} />
                  </IconButton>
                </Grid>
                <Grid item xs={9}>
                  <TableContainer component={Paper}>
                    <Table aria-label="simple table" xs={12}>
                      <TableHead>
                        <TableRow>
                          <TableCell align="center">CrimeTime</TableCell>
                          <TableCell align="center">CrimeLocation</TableCell>
                          {this.state.crimes.map((crime, index) => (
                            <TableCell align="center" key={index}>CrimeName & Count</TableCell>
                          ))}
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {this.state.crimes[0].map((crime,index) => (
                          <TableRow key={index}>
                            <TableCell align="center">{crime.crimeTime}</TableCell>
                            <TableCell align="center">{this.state.locationData}</TableCell>
                            {this.state.crimes.map((crimeItem, key) => (
                              <TableCell key={key} align="center">{crimeItem[index].crimeName}  :  {crimeItem[index].count}</TableCell>
                            ))}
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </Grid>
                <Grid item xs={1}>
                <IconButton onClick={this.changeDisplay}>
                    <ArrowForwardIcon style={{width: 60, height: 60}} />
                  </IconButton>
                </Grid>
              </Grid> : 
              <Grid container justify="center" alignItems="center" direction="row">
                <Grid item xs={1}>
                  <IconButton onClick={this.changeDisplay}>
                    <ArrowBackIcon style={{width: 60, height: 60}} />
                  </IconButton>
                </Grid>
                <Grid item xs={9}>
                  <MyChart categories={this.state.categories} series={this.state.series}/>
                </Grid>
                <Grid item xs={1}>
                  <IconButton onClick={this.changeDisplay}>
                    <ArrowForwardIcon style={{width: 60, height: 60}} />
                  </IconButton>
                </Grid>
              </Grid>}
          </div> : null}
        </div>
      </ThemeProvider>
    );
  }
}

export default SearchForm;