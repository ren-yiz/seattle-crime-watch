import React from 'react';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import {
  Grid
} from '@material-ui/core';
import MyChart from '../chart/MyChart'

class Main extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      crimes: [[{crimeName: "A", crimeTime: "1", count: 100}, {crimeName: "A", crimeTime: "2", count: 200}, {crimeName: "A", crimeTime: "3", count: 300}]
      ,[{crimeName: "B", crimeTime: "1", count: 10}, {crimeName: "B", crimeTime: "2", count: 20}, {crimeName: "B", crimeTime: "3", count: 30}]
      ,[{crimeName: "C", crimeTime: "1", count: 1}, {crimeName: "C", crimeTime: "2", count: 2}, {crimeName: "C", crimeTime: "3", count: 3}]
      ,[{crimeName: "D", crimeTime: "1", count: 1000}, {crimeName: "D", crimeTime: "2", count: 2000}, {crimeName: "D", crimeTime: "3", count: 3000}]],
      categories: [],
      series: []
    }
    this.searchCrimes=this.searchCrimes.bind(this);
  }

  componentDidMount() {
    this.searchCrimes();
  }

  searchCrimes() {
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

  render() {
    return this.state.crimes ? (
      <Grid container>
        <Grid item xs={9}>
          <TableContainer component={Paper}>
            <Table aria-label="simple table" xs={6}>
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
                    <TableCell align="center">98107</TableCell>
                    {this.state.crimes.map((crimeItem, key) => (
                      <TableCell key={key} align="center">{crimeItem[index].crimeName}  :  {crimeItem[index].count}</TableCell>
                    ))}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Grid>
        {/* <Grid item xs={6}>
          {this.state.categories.length > 0 ? <MyChart categories={this.state.categories} series={this.state.series}/> : null}
        </Grid> */}
      </Grid>
    ) : null;
  }
}

export default Main;