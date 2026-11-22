<script lang="ts">
    import githubIcon from "../assets/github.svg";
    export let t: any;
</script>

<footer
    class="site-footer"
    data-section="4">
    <div class="footer-top">
        <div class="footer-intro">
            <img
                src={`${import.meta.env.BASE_URL}icon.webp`}
                alt={`${t.appName} icon`} />
            <h2>{t.appName}<span class="brand-dot">.</span></h2>
            <p>{t.footerIntro}</p>
        </div>
        <div class="footer-links">
            <div>
                <b>{t.product}</b>
                <a href="#collect">{t.productLinks[0]}</a>
                <a href="#statistics">{t.productLinks[1]}</a>
                <a href="#insights">{t.productLinks[2]}</a>
                <a href="#transfer">{t.productLinks[3]}</a>
            </div>
            <div>
                <b>{t.resources}</b>
                <a
                    href="https://github.com/Iceberry-qdd/NotifyBox"
                    target="_blank"
                    rel="noreferrer">
                    {t.github}
                </a>
                <a
                    href="https://github.com/Iceberry-qdd/NotifyBox/releases"
                    target="_blank"
                    rel="noreferrer">{t.changelog}</a>
                <a
                    href="https://github.com/Iceberry-qdd/NotifyBox/issues"
                    target="_blank"
                    rel="noreferrer">{t.feedback}</a>
                <a
                    href="https://github.com/Iceberry-qdd/NotifyBox/releases/latest/download/NotifyBox.apk"
                    download>{t.downloadResource}</a>
                <a
                    href="https://icehub.top/profile/NotifyBox"
                    target="_blank"
                    rel="noreferrer">{t.blog}</a>
            </div>
            <div>
                <b>{t.friendLinks}</b>
                <a
                    class="footer-affiliate"
                    href="https://icehub.top"
                    target="_blank"
                    rel="noreferrer">
                    <img
                        src={`${import.meta.env.BASE_URL}icehub-icon.png`}
                        alt="" />
                    <span>{t.icehub}</span>
                </a>
                <a
                    class="footer-affiliate"
                    href="https://douersepper.top"
                    target="_blank"
                    rel="noreferrer">
                    <img
                        src="https://douersepper.top/favicon.png"
                        alt="" />
                    <span>{t.lvji}</span>
                </a>
            </div>
        </div>
    </div>
    <div class="footer-bottom">
        <span
            >© 2026 {t.appName} · made by
            <a
                href="https://icehub.top/profile/Iceberry"
                target="_blank"
                rel="noreferrer">Iceberry🥰</a>
        </span>
        <a
            class="footer-icon-slot"
            href="https://github.com/Iceberry-qdd/NotifyBox"
            target="_blank"
            rel="noreferrer"
            aria-label="GitHub">
            <img
                src={githubIcon}
                alt="GitHub" />
        </a>
    </div>
</footer>

<style>
    .site-footer {
        min-height: 54svh;
        scroll-snap-align: start;
        background: var(--bg-soft);
        padding: 4.25rem clamp(1.5rem, 8vw, 8.75rem) 1.5rem;
        display: flex;
        flex-direction: column;
        justify-content: center;
        gap: 3.4375rem;
        position: relative;
    }

    .site-footer::before {
        content: "";
        position: absolute;
        top: 0;
        left: 8vw;
        right: 8vw;
        height: 0.0625rem;
        background: var(--line);
    }

    .footer-top,
    .footer-bottom {
        width: min(77.5rem, 100%);
        margin: 0 auto;
    }

    .footer-top {
        display: flex;
        justify-content: space-between;
        gap: 4.375rem;
    }

    .footer-intro {
        max-width: 20rem;
    }
    .brand-dot {
        color: var(--accent);
    }
    .footer-intro img {
        width: 2.375rem;
        height: 2.375rem;
        border-radius: 0.6875rem;
    }
    .footer-intro h2 {
        font-size: 1.875rem;
        margin: 1.125rem 0 0.75rem;
    }
    .footer-intro p {
        color: var(--muted);
        font-size: 0.75rem;
        line-height: 1.7;
    }

    .footer-links {
        display: flex;
        gap: clamp(2.125rem, 6vw, 6.25rem);
    }

    .footer-links div {
        display: flex;
        flex-direction: column;
        gap: 0.8125rem;
        min-width: 6.25rem;
    }

    .footer-links b {
        color: var(--faint);
        font-family: var(--mono);
        font-size: 0.75rem;
        text-transform: uppercase;
        letter-spacing: 0.1em;
        margin-bottom: 0.3125rem;
    }

    .footer-links a {
        color: var(--muted);
        font-size: 0.75rem;
        transition: color 0.2s ease;
    }

    .footer-links a:hover {
        color: var(--text);
    }

    .footer-bottom {
        margin-top: 3.75rem;
        border-top: 0.0625rem solid var(--line);
        padding-top: 1.1875rem;
        color: var(--faint);
        font-family: var(--mono);
        font-size: 0.75rem;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 1.25rem;
    }

    .footer-icon-slot {
        width: 2.25rem;
        height: 2.25rem;
        display: grid;
        place-items: center;
        border: 1px solid var(--line-bright);
        border-radius: 50%;
        transition:
            border-color 0.2s ease,
            transform 0.2s ease;
    }

    .footer-icon-slot:hover {
        border-color: var(--accent);
        transform: translateY(-0.125rem);
    }

    .footer-icon-slot img {
        width: 1.125rem;
        height: 1.125rem;
        object-fit: contain;
        filter: none;
    }

    :global(:root:not([data-theme="light"])) .footer-icon-slot img {
        filter: invert(1);
    }

    .footer-affiliate {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
    }

    .footer-affiliate img {
        width: 1.2rem;
        height: auto;
        object-fit: cover;
    }

    .footer-bottom > span a {
        color: var(--text);
        text-decoration: underline;
        text-decoration-color: var(--accent);
        text-underline-offset: 0.1875rem;
    }

    @media (max-width: 640px) {
        .site-footer {
            min-height: 68svh;
            padding: 4.5rem 1.5rem 1.25rem;
            gap: 2.1875rem;
        }

        .footer-top {
            flex-direction: column;
            gap: 2.125rem;
        }

        .footer-links {
            justify-content: space-between;
            gap: 0.9375rem;
        }

        .footer-links div {
            min-width: 0;
        }

        .footer-bottom {
            flex-direction: row;
            flex-wrap: wrap;
            gap: 0.5rem;
            margin-top: 0;
        }
    }
</style>
