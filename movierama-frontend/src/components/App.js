import './App.css';
import Header from "./Header/Header";
import {useEffect, useState} from "react";
import api from "../api/Api";
import UserMoviesList from "./MoviesList/UserMoviesList";
import MoviesList from "./MoviesList/MoviesList";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import ActivateAccount from "./AccountActivation/AccountActivation";

function App() {
    const [token, setToken] = useState(null);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [userVotes, setUserVotes] = useState([]);

    const handleLogin = (newToken, userInfo) => {
        setToken(newToken);
        setUser(userInfo);
        if (newToken !== undefined) {
            localStorage.setItem('token', newToken);
            api.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
        } else {
            localStorage.removeItem('token');
            delete api.defaults.headers.common['Authorization'];
            setUserVotes([]);
        }
    };

    const checkTokenValidity = async (storedToken) => {
        try {
            if (storedToken) {
                const response = await api.get('/api/v1/user/info', {
                    headers: {
                        'Authorization': `Bearer ${storedToken}`
                    }
                });
                if (response.data) {
                    setUser(response.data);
                }
            } else {
                console.log('User is logged out');
            }
        } catch (error) {
            console.error('Error checking token validity:', error);
        }
    };

    const fetchUserVotes = async () => {
        try {
            const response = await api.get('/api/v1/user/votes', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            setUserVotes(response.data);
        } catch (error) {
            console.error('Error fetching user votes:', error);
        }
    };


    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        if (storedToken) {
            setToken(storedToken);
            checkTokenValidity(storedToken).then(r => r);
            setLoading(false);
        }
        setLoading(false);
    }, []);

    useEffect(() => {
        if (user) {
            fetchUserVotes().then(r => r);
        }
    }, [user]);

    return (
        <Router>
            <div className="App">
                {loading ? (
                    <div>Loading...</div>
                ) : (
                    <>
                        <Header onLogin={handleLogin} user={user}/>
                        <Routes>
                            <Route
                                exact
                                path="/"
                                element={<MoviesList user={user} userVotes={userVotes} setUserVotes={setUserVotes}/>}
                            />
                            <Route
                                path="/user/:userId/movies"
                                element={<UserMoviesList user={user} userVotes={userVotes}
                                                         setUserVotes={setUserVotes}/>}
                            />
                            <Route path="/activate-account" element={<ActivateAccount/>}/>
                        </Routes>
                    </>
                )}
            </div>
        </Router>
    );
}

export default App;
