import React, {useState} from 'react';
import {Button, Form, Modal} from 'react-bootstrap';
import api from '../../../api/Api';


const LoginModal = ({show, handleClose, onLogin}) => {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleEmailChange = (e) => setEmail(e.target.value);
    const handlePasswordChange = (e) => setPassword(e.target.value);

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await api.post('/api/v1/auth/authenticate', {
                email,
                password
            }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const {token} = response.data;
            // Fetch user info after successful login
            const userInfoResponse = await api.get('/api/v1/user/info', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            const user = userInfoResponse.data;
            onLogin(token, user);
            handleClose();
        } catch (err) {
            setError(err.response ? err.response.data.errorDescription : 'An error occurred');
        }
    };

    return (
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>Login</Modal.Title>
            </Modal.Header>
            <Form onSubmit={handleSubmit}>
            <Modal.Body>
                    <Form.Group controlId="formBasicEmail">
                        <Form.Label>Email address</Form.Label>
                        <Form.Control type="email" placeholder="Enter email" value={email} onChange={handleEmailChange}
                                      required/>
                    </Form.Group>

                    <Form.Group controlId="formBasicPassword">
                        <Form.Label>Password</Form.Label>
                        <Form.Control type="password" placeholder="Password" value={password}
                                      onChange={handlePasswordChange} required/>
                    </Form.Group>
                    {error && <p className="text-danger">{error}</p>}
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" type="submit">
                    Login
                </Button>
                <Button variant="secondary" onClick={handleClose}>
                    Close
                </Button>
            </Modal.Footer>
            </Form>
        </Modal>
    );
};

export default LoginModal;
