import React from 'react';
import {
    BrowserRouter as Router,    
    Routes,
    Route,    
} from "react-router-dom";

import Tier from './tier';
import Order from './order';

const Webpages = () => {
    return(
        <Router>
            <Routes>
                <Route path = "/info/:customerId" element = {<Tier />} />
                <Route path = "/order/:customerId" element = {<Order />} />
            </Routes>
        </Router>
    );
};

export default Webpages;