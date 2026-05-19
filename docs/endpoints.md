# API Endpoints

## Base path

```text
/api
```

This document describes the main REST endpoints exposed by the Wine Social REST API.

---

## Users

| Method | Endpoint | Description |
|---|---|---|
| POST | `/usuario` | Create a new user |
| GET | `/usuario/{ID_usuario}` | Get user information |
| PUT | `/usuario/{ID_usuario}` | Update user information |
| DELETE | `/usuario/{ID_usuario}` | Delete user |

---

## Wines

| Method | Endpoint | Description |
|---|---|---|
| POST | `/usuario/{ID_usuario}/vino` | Add a wine for a user |
| GET | `/usuario/{ID_usuario}/vino` | Get all wines from a user |
| PUT | `/usuario/{ID_usuario}/vino/{ID_vino}` | Update a wine |
| DELETE | `/usuario/{ID_usuario}/vino/{ID_vino}` | Delete a wine |

---

## Followers

| Method | Endpoint | Description |
|---|---|---|
| POST | `/usuario/{ID_usuario}/seguidor` | Add a follower |
| GET | `/usuario/{ID_usuario}/seguidor` | Get the followers of a user |
| DELETE | `/usuario/{ID_usuario}/seguidor/{ID_seguidor}` | Remove a follower |

---

## Recommendations

| Method | Endpoint | Description |
|---|---|---|
| GET | `/usuario/{ID_usuario}/seguidor/{ID_seguidor}/vinos` | Get wines from a followed user |
| GET | `/usuario/{ID_usuario}/recomendacion` | Get wine recommendations for a user |

---

## Full endpoint list with base path

| Method | Full endpoint | Description |
|---|---|---|
| POST | `/api/usuario` | Create a new user |
| GET | `/api/usuario/{ID_usuario}` | Get user information |
| PUT | `/api/usuario/{ID_usuario}` | Update user information |
| DELETE | `/api/usuario/{ID_usuario}` | Delete user |
| POST | `/api/usuario/{ID_usuario}/vino` | Add a wine for a user |
| GET | `/api/usuario/{ID_usuario}/vino` | Get all wines from a user |
| PUT | `/api/usuario/{ID_usuario}/vino/{ID_vino}` | Update a wine |
| DELETE | `/api/usuario/{ID_usuario}/vino/{ID_vino}` | Delete a wine |
| POST | `/api/usuario/{ID_usuario}/seguidor` | Add a follower |
| GET | `/api/usuario/{ID_usuario}/seguidor` | Get the followers of a user |
| DELETE | `/api/usuario/{ID_usuario}/seguidor/{ID_seguidor}` | Remove a follower |
| GET | `/api/usuario/{ID_usuario}/seguidor/{ID_seguidor}/vinos` | Get wines from a followed user |
| GET | `/api/usuario/{ID_usuario}/recomendacion` | Get wine recommendations for a user |

---

## Data format

The API consumes and produces data in JSON format.

```http
Content-Type: application/json
Accept: application/json
```

---

## Notes

This API follows a REST-style structure using HTTP methods:

- `GET` to retrieve data
- `POST` to create data
- `PUT` to update data
- `DELETE` to remove data

The API is implemented in Java using Jersey/JAX-RS and is deployed as a WAR application.