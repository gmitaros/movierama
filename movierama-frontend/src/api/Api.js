import axios from 'axios';
const host = process.env.REACT_APP_HOST;

const api = axios.create({
    baseURL: `${host}`,
    headers: {
        'Content-Type': 'application/json',
    },
});

export default api;
