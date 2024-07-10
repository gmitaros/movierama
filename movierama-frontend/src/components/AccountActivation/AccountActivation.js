import React, {useEffect, useState} from 'react';
import {useLocation} from 'react-router-dom';
import api from "../../api/Api";
import {Button, Col, Container, Row} from "react-bootstrap";

function ActivateAccount() {
    const [message, setMessage] = useState('');
    const location = useLocation();
    const activationCode = new URLSearchParams(location.search).get('activation-code');

    useEffect(() => {
        async function activateAccount() {
            try {
                const response = await api.get(`/api/v1/auth/activate-account?activation-code=${activationCode}`);

                await response;
                setMessage('Account activation was successful. Now you can login');

            } catch (error) {
                if (error.response.data.errorCode === 305) {
                    setMessage('Token expired. We have sent you a new activation link.');
                } else {
                    setMessage('Account activation failed');
                }
            }
        }

        if (activationCode) {
            activateAccount();
        }
    }, [activationCode]);

    return (
        <Container className="mt-5">
            <Row className="justify-content-center">
                <Col md={8}>
                    <div className="bg-white p-5 rounded-lg shadow-lg">
                        <h1 className="text-2xl font-bold mb-4">Account Activation</h1>
                        <p className="text-lg">{message}</p>
                        <Button variant="primary" className="mt-4" href="/">Go to Homepage</Button>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export default ActivateAccount;
