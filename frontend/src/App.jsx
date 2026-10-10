import React from 'react';

export default function App() {
  return (
    <div className="d-flex flex-column min-vh-100">
      {/* Navigation Bar */}
      <nav className="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
        <div className="container">
          <a className="navbar-brand fw-bold d-flex align-items-center gap-2" href="#home">
            <span className="badge bg-primary fs-6">CRMS</span>
            <span>Car Rental Management System</span>
          </a>
          <button
            className="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarNav"
            aria-controls="navbarNav"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon"></span>
          </button>
          <div className="collapse navbar-collapse" id="navbarNav">
            <ul className="navbar-nav ms-auto align-items-center gap-2">
              <li className="nav-item">
                <a className="nav-link active" href="#overview">Overview</a>
              </li>
              <li className="nav-item">
                <a className="nav-link" href="#tech-stack">Tech Stack</a>
              </li>
              <li className="nav-item">
                <a className="nav-link" href="#architecture">Architecture</a>
              </li>
              <li className="nav-item">
                <span className="badge bg-success-subtle text-success border border-success-subtle px-3 py-2">
                  System Ready (Initial Setup)
                </span>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="hero-section text-center">
        <div className="container py-4">
          <div className="row justify-content-center">
            <div className="col-lg-9">
              <div className="d-inline-flex align-items-center gap-2 badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-2 mb-3">
                <span>Java Full Stack Developer Portfolio</span>
              </div>
              <h1 className="display-4 fw-bold mb-3">Car Rental Management System</h1>
              <p className="lead text-light opacity-75 mb-4">
                Enterprise full-stack foundation built with Java 17, Spring Boot 3.3, MySQL, and React 18 with Vite and Bootstrap 5.
              </p>
              <div className="d-flex flex-wrap justify-content-center gap-2">
                <span className="badge bg-secondary badge-tech">Java 17</span>
                <span className="badge bg-secondary badge-tech">Spring Boot 3.3.4</span>
                <span className="badge bg-secondary badge-tech">Spring Data JPA</span>
                <span className="badge bg-secondary badge-tech">MySQL Driver</span>
                <span className="badge bg-secondary badge-tech">React 18</span>
                <span className="badge bg-secondary badge-tech">Vite 5</span>
                <span className="badge bg-secondary badge-tech">Bootstrap 5</span>
                <span className="badge bg-secondary badge-tech">Axios</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Main Content Area */}
      <main className="container my-5 flex-grow-1" id="overview">
        {/* Project Setup Status Card */}
        <div className="card shadow-sm border-0 mb-5">
          <div className="card-body p-4">
            <div className="d-flex align-items-center justify-content-between flex-wrap gap-2 mb-3">
              <h3 className="h5 fw-bold mb-0">Project Initialization Status</h3>
              <span className="badge bg-success px-3 py-2">Configuration Complete</span>
            </div>
            <p className="text-muted mb-4">
              The project structure has been established according to standard full-stack enterprise conventions.
              Backend dependencies and database abstraction are configured via environment variables, and the frontend
              is ready for component and API integration.
            </p>

            <div className="row g-3">
              <div className="col-md-6">
                <div className="p-3 border rounded-3 bg-light h-100">
                  <div className="d-flex align-items-center gap-2 mb-2">
                    <span className="badge bg-primary">Backend</span>
                    <strong className="text-dark">Spring Boot 3.x (Java 17)</strong>
                  </div>
                  <ul className="list-unstyled small text-secondary mb-0">
                    <li className="mb-1">&#10003; Maven project inside <code>backend/</code></li>
                    <li className="mb-1">&#10003; Package structure: <code>com.carrental</code></li>
                    <li className="mb-1">&#10003; Spring Web, Data JPA, MySQL Connector &amp; Validation</li>
                    <li className="mb-1">&#10003; Dynamic environment-based properties configured</li>
                    <li className="mb-0">&#10003; Maven wrapper (<code>mvnw</code>) configured</li>
                  </ul>
                </div>
              </div>
              <div className="col-md-6">
                <div className="p-3 border rounded-3 bg-light h-100">
                  <div className="d-flex align-items-center gap-2 mb-2">
                    <span className="badge bg-info text-dark">Frontend</span>
                    <strong className="text-dark">React 18 + Vite (JavaScript)</strong>
                  </div>
                  <ul className="list-unstyled small text-secondary mb-0">
                    <li className="mb-1">&#10003; React 18 SPA inside <code>frontend/</code></li>
                    <li className="mb-1">&#10003; Native JavaScript Vite bundling</li>
                    <li className="mb-1">&#10003; Bootstrap 5 styling integration</li>
                    <li className="mb-1">&#10003; Axios client module configured (<code>src/api.js</code>)</li>
                    <li className="mb-0">&#10003; Development &amp; production build scripts</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* System Architecture Grid */}
        <h4 className="fw-bold mb-3" id="architecture">Module Roadmap &amp; Architecture</h4>
        <div className="row g-4 mb-5">
          <div className="col-md-4">
            <div className="card h-100 feature-card p-3">
              <div className="card-body">
                <div className="badge bg-primary-subtle text-primary mb-2">Vehicle Fleet</div>
                <h5 className="card-title fw-bold">Fleet Management</h5>
                <p className="card-text text-muted small">
                  Vehicle inventory tracking, category specifications (Sedan, SUV, Luxury, Electric), status
                  indicators (Available, Rented, Maintenance), and pricing rules.
                </p>
              </div>
            </div>
          </div>

          <div className="col-md-4">
            <div className="card h-100 feature-card p-3">
              <div className="card-body">
                <div className="badge bg-success-subtle text-success mb-2">Bookings</div>
                <h5 className="card-title fw-bold">Rental Reservations</h5>
                <p className="card-text text-muted small">
                  Booking lifecycles from reservation, check-in, pickup date validation, vehicle inspection,
                  and return processing with automated duration billing.
                </p>
              </div>
            </div>
          </div>

          <div className="col-md-4">
            <div className="card h-100 feature-card p-3">
              <div className="card-body">
                <div className="badge bg-warning-subtle text-warning-emphasis mb-2">Accounts</div>
                <h5 className="card-title fw-bold">Customer Profiles</h5>
                <p className="card-text text-muted small">
                  Customer registration, driver license verification, rental history tracking, and contact management
                  structured with Spring Data JPA entities.
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Configuration Guide */}
        <div className="card border-0 bg-light p-4 rounded-3">
          <h5 className="fw-bold mb-3">Database &amp; Environment Configuration</h5>
          <p className="small text-muted mb-3">
            The backend is configured to dynamically read connection credentials from environment variables,
            preventing hardcoded secrets from entering source control:
          </p>
          <pre className="bg-dark text-light p-3 rounded-2 small overflow-auto mb-0">
{`# Environment Variables:
DB_HOST=localhost
DB_PORT=3306
DB_NAME=car_rental_db
DB_USERNAME=your_username
DB_PASSWORD=your_password
SERVER_PORT=8080`}
          </pre>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-dark text-light py-4 mt-auto border-top border-secondary">
        <div className="container text-center small text-secondary">
          <p className="mb-1">Car Rental Management System &bull; Java Full Stack Developer Project</p>
          <p className="mb-0 text-secondary opacity-75">Spring Boot 3.3 &bull; React 18 &bull; MySQL &bull; Bootstrap 5</p>
        </div>
      </footer>
    </div>
  );
}
