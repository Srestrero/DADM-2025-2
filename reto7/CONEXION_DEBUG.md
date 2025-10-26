# 🔧 Guía de Diagnóstico de Conexión - Triqui Online

## ✅ Verificaciones Antes de Jugar

### 1. **Verificar que el Servidor está Corriendo**

Desde tu terminal, verifica que el servidor esté activo:

```bash
# Ver si el servidor está escuchando en el puerto 8080
netstat -tuln | grep 8080

# O probar con curl
curl http://localhost:8080/health
```

Si el servidor está corriendo, deberías ver: `{"status": "healthy"}`

---

### 2. **Obtener tu IP LAN**

```bash
# Linux/Mac
ip addr show | grep "inet " | grep -v 127.0.0.1

# O más simple
hostname -I
```

Busca una IP que empiece con `192.168.x.x` o `10.x.x.x`

---

### 3. **Configuración según Dispositivo**

#### 📱 **Emulador de Android Studio:**
- **IP a usar:** `10.0.2.2`
- **Puerto:** `8080`
- **URL completa:** `http://10.0.2.2:8080/health`

**Por qué `10.0.2.2`?**
- El emulador tiene una red virtual separada
- `10.0.2.2` es una IP especial que apunta a `localhost` de tu PC

#### 📱 **Dispositivo Real (celular/tablet):**
- **IP a usar:** Tu IP LAN (ej: `192.168.1.10`)
- **Puerto:** `8080`
- **URL completa:** `http://192.168.1.10:8080/health`

**Requisitos:**
- Tu celular y PC deben estar en la MISMA red WiFi
- El firewall no debe bloquear el puerto 8080

---

## 🐛 Solución de Problemas

### ❌ Problema: "No se pudo conectar al servidor"

#### **Desde el Emulador:**

1. **Verifica que el servidor corre en tu PC:**
   ```bash
   curl http://localhost:8080/health
   ```

2. **Si el servidor está en Python/Node, asegúrate que escuche en `0.0.0.0`:**
   ```python
   # Python (FastAPI/Flask)
   uvicorn main:app --host 0.0.0.0 --port 8080
   # o
   app.run(host='0.0.0.0', port=8080)
   ```
   
   ```javascript
   // Node.js
   app.listen(8080, '0.0.0.0', () => {
     console.log('Server running on port 8080');
   });
   ```

3. **Prueba desde el emulador con adb:**
   ```bash
   # Conectar al emulador
   adb shell
   
   # Probar conexión
   curl http://10.0.2.2:8080/health
   ```

#### **Desde Dispositivo Real:**

1. **Verifica que tu PC y celular estén en la misma red:**
   ```bash
   # En tu PC, obtén tu IP
   hostname -I
   
   # Desde tu celular, prueba hacer ping a esa IP
   # (usa alguna app de terminal en Android como Termux)
   ping 192.168.1.10
   ```

2. **Verifica el firewall:**
   ```bash
   # Linux - Permitir puerto 8080
   sudo ufw allow 8080
   
   # O desactivar temporalmente
   sudo ufw disable
   ```

3. **Prueba acceder desde el navegador del celular:**
   - Abre Chrome en tu celular
   - Navega a: `http://192.168.1.10:8080/health`
   - Deberías ver: `{"status": "healthy"}`

---

## 📊 Checklist de Conexión

### Emulador:
- [ ] Servidor corriendo en tu PC (`curl http://localhost:8080/health`)
- [ ] Servidor escuchando en `0.0.0.0` (no solo `localhost`)
- [ ] Usar IP `10.0.2.2` en la app
- [ ] Puerto `8080` configurado

### Dispositivo Real:
- [ ] Servidor corriendo en tu PC
- [ ] PC y celular en la misma red WiFi
- [ ] Firewall permite puerto 8080
- [ ] Usar IP LAN de tu PC (ej: `192.168.1.10`)
- [ ] Puerto `8080` configurado
- [ ] Puedes acceder desde el navegador del celular

---

## 🎮 Para Jugar Entre Emulador y Celular

Si quieres que el **emulador** juegue contra un **celular real**:

1. **Servidor:** Debe correr en tu PC en la IP LAN (192.168.1.10)
2. **Emulador:** Configurar con `10.0.2.2:8080`
3. **Celular:** Configurar con `192.168.1.10:8080`

Ambos dispositivos se conectarán al mismo servidor, pero usando diferentes rutas de red.

---

## 📝 Comandos Útiles de Debug

### Ver logs del emulador:
```bash
adb logcat | grep GameApiClient
```

### Ver conexiones activas:
```bash
netstat -an | grep 8080
```

### Probar el servidor desde diferentes lugares:
```bash
# Desde tu PC
curl http://localhost:8080/health

# Desde la red LAN (otro dispositivo)
curl http://192.168.1.10:8080/health
```

---

## 🆘 Último Recurso

Si nada funciona, intenta:

1. **Reiniciar el emulador** de Android Studio
2. **Reiniciar el servidor**
3. **Usar ngrok** para tunelizar (avanzado):
   ```bash
   ngrok http 8080
   # Usa la URL https que te da ngrok
   ```

---

¿Más ayuda? Revisa los logs en Android Studio (Logcat) buscando por "GameApiClient"


