# Keycloak Automated Setup

## Overview

Keycloak is configured to automatically import realm configuration on startup, eliminating manual configuration steps.

## What's Included

### Realm Configuration
- **Realm Name**: `maca-fin`
- **Display Name**: Maca Finance
- **Security**: Brute force protection enabled, SSL required for external connections

### Client Configuration
- **Client ID**: `backend`
- **Type**: Confidential client with service accounts
- **Secret**: Configured via `MACA_FIN_OIDC_REALM_SECRET` environment variable
- **Features**: Authorization services enabled, direct access grants enabled

### Roles
- `admin` - Administrator role
- `user` - Standard user role

### Default Users
- **Admin User**
  - Username: `admin`
  - Password: `admin123`
  - Email: `admin@maca-fin.local`
  - Roles: admin, user

- **Test User**
  - Username: `testuser`
  - Password: `test123`
  - Email: `test@maca-fin.local`
  - Roles: user

## Configuration Files

### keycloak-realm.json
Contains the complete realm configuration including clients, roles, users, and security settings.

### keycloak.conf
Database connection configuration for Keycloak to use PostgreSQL.

### compose.yml
Updated with:
- Volume mount for `keycloak-realm.json` at `/opt/keycloak/data/import/realm.json`
- Command changed to `start --import-realm`

## Usage

### First Time Setup
```bash
docker compose up -d
```

Keycloak will automatically:
1. Connect to PostgreSQL database
2. Import the realm configuration
3. Create clients, roles, and users
4. Be ready for authentication

### Updating Configuration

1. Edit `keycloak-realm.json` with your changes
2. Recreate the Keycloak container:
```bash
docker compose down keycloak
docker compose up -d keycloak
```

### Important Notes

- The realm import only happens if the realm doesn't already exist
- To force a reimport, delete the Keycloak database or use a different realm name
- Client secrets use environment variable substitution: `${MACA_FIN_OIDC_REALM_SECRET}`
- Default user passwords should be changed in production

## Environment Variables

Required variables in `.env`:
```bash
MACA_FIN_OIDC_REALM_SECRET=your-client-secret
MACA_FIN_OIDC_ADMIN_USERNAME=admin-username
MACA_FIN_OIDC_ADMIN_PASSWORD=admin-password
```

## Customization

### Adding Users
Add to the `users` array in `keycloak-realm.json`:
```json
{
  "username": "newuser",
  "enabled": true,
  "emailVerified": true,
  "email": "newuser@example.com",
  "credentials": [
    {
      "type": "password",
      "value": "password123",
      "temporary": false
    }
  ],
  "realmRoles": ["user"]
}
```

### Adding Clients
Add to the `clients` array in `keycloak-realm.json`:
```json
{
  "clientId": "frontend",
  "enabled": true,
  "publicClient": true,
  "redirectUris": ["http://localhost:3000/*"],
  "webOrigins": ["http://localhost:3000"]
}
```

### Modifying Token Lifespans
Edit these fields in `keycloak-realm.json`:
- `accessTokenLifespan` - Access token validity (seconds)
- `ssoSessionIdleTimeout` - SSO session idle timeout
- `ssoSessionMaxLifespan` - Maximum SSO session duration

## Troubleshooting

### Realm Not Importing
Check Keycloak logs:
```bash
docker compose logs keycloak
```

### Client Secret Not Working
Verify the environment variable is set correctly:
```bash
docker compose exec keycloak env | grep MACA_FIN_OIDC_REALM_SECRET
```

### Users Not Created
Ensure `emailVerified` is set to `true` if email verification is required.

## Security Recommendations

1. Change default user passwords immediately
2. Use strong, randomly generated client secrets
3. Enable HTTPS in production
4. Review and adjust token lifespans based on security requirements
5. Enable additional security features like OTP for admin accounts
