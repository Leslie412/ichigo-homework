import React, { useEffect, useState } from 'react';
import Progressbar from './progressbar';
import { useParams } from 'react-router-dom'
import './webpages.css'

const Tier = () => {
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);
    const [user, setUser] = useState(null);
    const baseUrl = "http://localhost:8080/tiers/";
    let params = useParams();
    let fullUrl = baseUrl + params.customerId;

    useEffect(() => {        
        fetch(fullUrl)
        .then(res => res.json())
        .then(
            (data) => {
                setIsLoaded(true);
                setUser(data)
            },
            (error) => {
                setIsLoaded(true);
                setError(error);
            }
        )
    })

    if (error) {
        return <div> Error: {error.message}</div>;
    } else if (!isLoaded) {
        return <div>Loading...</div>;
    } else {
        // calculate progress bar
        let progress = 0;
        switch (user.tier) {
            case 'Silver':
                progress = 50000 - user.amount4next < 0 ? 100 : (50000 - user.amount4next) / 500;
                break;
            case 'Bronze': 
                progress = 10000 - user.amount4next < 0 ? 100 : (10000 - user.amount4next) / 100;
                break;
            default:
                progress = 100;
        }
        return (
            <ul>               
                <table>
                    <tbody>
                        <tr>
                            <td>
                                Tier
                            </td>
                            <td>
                                {user.tier}
                            </td>                         
                        </tr>
                        <tr>
                            <td>
                                Calculation Start Date
                            </td>
                            <td>
                                {new Date(user.startdate).toLocaleDateString()}
                            </td> 
                        </tr>
                        <tr>
                            <td>
                                Amount Spent
                            </td>
                            <td>
                                {user.amountspent/100}
                            </td> 
                        </tr>
                        <tr>
                            <td>
                                Amount To Next Tier                            
                            </td>                            
                            <td>
                                <table>
                                    <tbody> 
                                        <tr>                             
                                    <td>
                                        {user.amount4next/100}
                                    </td>
                                    <td>
                                        {<Progressbar bgcolor="red" fgcolor="lightgreen" height={20} progress={progress} />}
                                    </td>
                                    </tr> 
                                    </tbody>
                                </table>

                            </td>
                           
                        </tr>

                        <tr>
                            <td>
                                Down Tier
                            </td>
                            <td>
                                {user.downtier}
                            </td> 
                        </tr>
                        <tr>
                            <td>
                                Down Date
                            </td>
                            <td>
                                {new Date(user.downdate).toLocaleDateString()}
                            </td> 
                        </tr>
                        <tr>
                            <td>
                                Amount to Keep Tier
                            </td>
                            <td>
                                {user.spendneeded/100}
                            </td> 
                        </tr>
                    </tbody>
                </table>                
            </ul>            
        )
    }    
}

export default Tier;