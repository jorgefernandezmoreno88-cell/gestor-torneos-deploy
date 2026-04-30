import { c as createComponent } from './astro-component_0s_TtBMU.mjs';
import 'piccolore';
import { l as renderComponent, r as renderTemplate, m as maybeRenderHead } from './entrypoint_BgRex8wb.mjs';
import { $ as $$MainLayout, r as renderScript } from './MainLayout_pCS74QOT.mjs';

const $$Login = createComponent(async ($$result, $$props, $$slots) => {
  return renderTemplate`${renderComponent($$result, "MainLayout", $$MainLayout, { "title": "Login - Gestor de Torneos", "description": "Inicia sesión", "data-astro-cid-sgpqyurt": true }, { "default": async ($$result2) => renderTemplate` ${maybeRenderHead()}<div class="container auth-container" data-astro-cid-sgpqyurt> <div class="card auth-card" data-astro-cid-sgpqyurt> <h1 data-astro-cid-sgpqyurt>Iniciar sesión</h1> <p class="auth-subtitle" data-astro-cid-sgpqyurt>Accede para gestionar torneos e inscripciones.</p> <div id="login-message" class="alert" style="display:none;" data-astro-cid-sgpqyurt></div> <form id="login-form" class="auth-form" data-astro-cid-sgpqyurt> <label for="email" data-astro-cid-sgpqyurt>Email</label> <input id="email" name="email" type="email" required data-astro-cid-sgpqyurt> <label for="password" data-astro-cid-sgpqyurt>Password</label> <input id="password" name="password" type="password" required data-astro-cid-sgpqyurt> <button type="submit" class="button button-primary" data-astro-cid-sgpqyurt>Entrar</button> </form> <p class="auth-extra" data-astro-cid-sgpqyurt>
¿No tienes cuenta? <a href="/register" data-astro-cid-sgpqyurt>Regístrate aquí</a> </p> </div> </div> ` })} ${renderScript($$result, "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/login.astro?astro&type=script&index=0&lang.ts")}`;
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/login.astro", void 0);

const $$file = "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/login.astro";
const $$url = "/login";

const _page = /*#__PURE__*/Object.freeze(/*#__PURE__*/Object.defineProperty({
  __proto__: null,
  default: $$Login,
  file: $$file,
  url: $$url
}, Symbol.toStringTag, { value: 'Module' }));

const page = () => _page;

export { page };
