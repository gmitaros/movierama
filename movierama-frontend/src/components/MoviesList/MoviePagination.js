import React from 'react';
import { Pagination } from 'react-bootstrap';

const MoviePagination = ({ currentPage, pageInfo, handlePageChange }) => {
    return (
        pageInfo.totalPages && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="col-span-3">
                    <Pagination>
                        <Pagination.First onClick={() => handlePageChange(1)} />
                        <Pagination.Prev onClick={() => handlePageChange(currentPage - 1)}
                                         disabled={currentPage === 1} />
                        <Pagination.Item
                            key={currentPage - 1}
                            onClick={() => handlePageChange(currentPage - 1)}
                            disabled={currentPage - 1 < 1}
                        >
                            {currentPage - 1}
                        </Pagination.Item>
                        <Pagination.Item
                            key={currentPage}
                            onClick={() => handlePageChange(currentPage)}
                        >
                            {currentPage}
                        </Pagination.Item>
                        <Pagination.Item
                            key={currentPage + 1}
                            onClick={() => handlePageChange(currentPage + 1)}
                            disabled={currentPage === pageInfo.totalPages}
                        >
                            {currentPage + 1}
                        </Pagination.Item>
                        <Pagination.Next onClick={() => handlePageChange(currentPage + 1)}
                                         disabled={currentPage === pageInfo.totalPages} />
                        <Pagination.Last onClick={() => handlePageChange(pageInfo.totalPages)} />
                    </Pagination>
                </div>
            </div>
        )
    );
}

export default MoviePagination;
