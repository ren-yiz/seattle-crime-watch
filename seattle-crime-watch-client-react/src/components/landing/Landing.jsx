import React from 'react';
import './css/Landing.css';
import { Link } from "react-router-dom";

class Landing extends React.Component {
    render() {
        return (
            <section className="landing">
                <div className="search-title">
                    <h1>Search Seattle Crime records</h1>
                    <Link to={"/search"} >
                        <button className="btn btn-primary" key="search-btn">Search</button>
                    </Link>
                </div>
            </section>

        );
    }
}
export default Landing;