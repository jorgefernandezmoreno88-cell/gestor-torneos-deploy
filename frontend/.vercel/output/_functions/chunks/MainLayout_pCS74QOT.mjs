import { c as createComponent } from './astro-component_0s_TtBMU.mjs';
import 'piccolore';
import { p as createRenderInstruction, m as maybeRenderHead, r as renderTemplate, l as renderComponent, h as addAttribute, q as renderHead, v as renderSlot } from './entrypoint_BgRex8wb.mjs';
import 'clsx';

async function renderScript(result, id) {
  const inlined = result.inlinedScripts.get(id);
  let content = "";
  if (inlined != null) {
    if (inlined) {
      content = `<script type="module">${inlined}</script>`;
    }
  } else {
    const resolved = await result.resolve(id);
    content = `<script type="module" src="${result.userAssetsBase ? (result.base === "/" ? "" : result.base) + result.userAssetsBase : ""}${resolved}"></script>`;
  }
  return createRenderInstruction({ type: "script", id, content });
}

const $$NavBar = createComponent(($$result, $$props, $$slots) => {
  return renderTemplate`${maybeRenderHead()}<nav class="navbar" data-astro-cid-v4dcqgr2> <ul class="nav-list" data-astro-cid-v4dcqgr2> <li data-astro-cid-v4dcqgr2><a href="/" data-astro-cid-v4dcqgr2>Inicio</a></li> <li data-astro-cid-v4dcqgr2><a href="/torneos" data-astro-cid-v4dcqgr2>Torneos</a></li> <li data-astro-cid-v4dcqgr2><a href="/ranking" data-astro-cid-v4dcqgr2>Ranking</a></li> <li data-astro-cid-v4dcqgr2><a href="/mis-torneos" data-astro-cid-v4dcqgr2>Mis torneos</a></li> <li data-astro-cid-v4dcqgr2><a href="/perfil" data-astro-cid-v4dcqgr2>Perfil</a></li> <li data-astro-cid-v4dcqgr2><a href="/register" id="nav-register" data-astro-cid-v4dcqgr2>Registro</a></li> <li data-astro-cid-v4dcqgr2><a href="/login" id="nav-login" data-astro-cid-v4dcqgr2>Login</a></li> <li data-astro-cid-v4dcqgr2> <button class="logout-btn" id="nav-logout" type="button" data-astro-cid-v4dcqgr2>Salir</button> </li> </ul> </nav> ${renderScript($$result, "D:/Descargas/gestor-torneos-clase-master/frontend/src/components/ui/NavBar.astro?astro&type=script&index=0&lang.ts")}`;
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/components/ui/NavBar.astro", void 0);

const $$Header = createComponent(($$result, $$props, $$slots) => {
  return renderTemplate`${maybeRenderHead()}<header class="header" data-astro-cid-hnhh3bfe> <div class="container" data-astro-cid-hnhh3bfe> <div class="header-content" data-astro-cid-hnhh3bfe> <div class="logo" data-astro-cid-hnhh3bfe> <h1 data-astro-cid-hnhh3bfe><a href="/" data-astro-cid-hnhh3bfe>Gestor de Torneos</a></h1> </div> ${renderComponent($$result, "NavBar", $$NavBar, { "data-astro-cid-hnhh3bfe": true })} </div> </div> </header>`;
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/components/ui/Header.astro", void 0);

const $$Footer = createComponent(($$result, $$props, $$slots) => {
  const Astro2 = $$result.createAstro($$props, $$slots);
  Astro2.self = $$Footer;
  return renderTemplate`${maybeRenderHead()}<footer class="footer" data-astro-cid-mh6itkc3> <div class="container" data-astro-cid-mh6itkc3> <p data-astro-cid-mh6itkc3>&copy; 2024 Gestor de Torneos. Desarrollado en Astro.</p> </div> </footer>`;
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/components/ui/Footer.astro", void 0);

const $$MainLayout = createComponent(($$result, $$props, $$slots) => {
  const Astro2 = $$result.createAstro($$props, $$slots);
  Astro2.self = $$MainLayout;
  const {
    title = "Gestor de Torneos",
    description = "Gestiona tus torneos deportivos de forma sencilla"
  } = Astro2.props;
  return renderTemplate`<html lang="es" data-astro-cid-ouamjn2i> <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><meta name="description"${addAttribute(description, "content")}><title>${title}</title>${renderHead()}</head> <body data-astro-cid-ouamjn2i> ${renderComponent($$result, "Header", $$Header, { "data-astro-cid-ouamjn2i": true })} <main class="main-content" data-astro-cid-ouamjn2i> ${renderSlot($$result, $$slots["default"])} </main> ${renderComponent($$result, "Footer", $$Footer, { "data-astro-cid-ouamjn2i": true })}</body></html>`;
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/layouts/MainLayout.astro", void 0);

export { $$MainLayout as $, renderScript as r };
