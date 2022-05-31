import React, { useEffect, useState } from 'react';
import Table from './reacttable';
import { useParams } from 'react-router-dom';

const Order = () => {
    const baseUrl = "http://localhost:8080/orders?customerid=";
    let params = useParams();
    let fullUrl = baseUrl + params.customerId;
    const [orders, setOrders] = useState([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);
    
    useEffect(() => {        
        fetch(fullUrl)
        .then(res => res.json())
        .then(
            (data) => {
                setIsLoaded(true);
                setOrders(data)
            },
            (error) => {
                setIsLoaded(true);
                setError(error);
            }
        )
    })

    const tableHead = {
        orderid: 'Order ID',
        orderdate: 'Order Date',
        ordertotal: 'Amount(Cents)',
      };

    orders.map( order => {
        order.orderdate = new Date(order.orderdate).toLocaleDateString();
        return order;      
    })
    
    if (error) {
        return <div> Error: {error.message}</div>;
    } else if (!isLoaded) {
        return <div>Loading...</div>;
    } else {
        return(
            <div>
                <h1>Customer Orders</h1>                
                <Table tableHead={tableHead} allData={orders} />                
            </div>
        );
    }
}

export default Order;