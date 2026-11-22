<script lang="ts">
    import { tick } from "svelte";
    import CursorRing from "./components/CursorRing.svelte";
    import PageScroll from "./components/PageScroll.svelte";
    import SectionRail from "./components/SectionRail.svelte";
    import Header from "./views/Header.svelte";
    import HeroSection from "./views/HeroSection.svelte";
    import StatisticsSection from "./views/StatisticsSection.svelte";
    import AnalysisSection from "./views/AnalysisSection.svelte";
    import TransferSection from "./views/TransferSection.svelte";
    import Footer from "./views/Footer.svelte";
    import { initialLanguage, initialTheme } from "./lib/preferences";
    import { copy, type Language, type Theme } from "./lib/siteContent";

    type ViewTransitionDocument = Document & {
        startViewTransition?: (updateCallback: () => void | Promise<void>) => { finished: Promise<void> };
    };

    let language: Language = initialLanguage();
    let theme: Theme = initialTheme();
    let activeSection = 0;
    let isScrolled = false;
    let isThemeTransitioning = false;
    let pageScroll: { scrollToSection: (index: number) => void };

    $: t = copy[language];

    if (typeof document !== "undefined") {
        document.documentElement.dataset.theme = theme;
        document.documentElement.lang = language === "zh" ? "zh-CN" : "en";
    }

    function toggleTheme(event: MouseEvent) {
        if (isThemeTransitioning) return;

        const button = event.currentTarget as HTMLElement;
        const bounds = button.getBoundingClientRect();
        const nextTheme: Theme = theme === "dark" ? "light" : "dark";
        const root = document.documentElement;

        root.style.setProperty("--theme-x", `${((bounds.left + bounds.width / 2) / window.innerWidth) * 100}%`);
        root.style.setProperty("--theme-y", `${((bounds.top + bounds.height / 2) / window.innerHeight) * 100}%`);
        isThemeTransitioning = true;

        const applyTheme = async () => {
            root.dataset.themeTransition = nextTheme === "dark" ? "to-dark" : "to-light";
            theme = nextTheme;
            root.dataset.theme = theme;
            localStorage.setItem("notifybox-theme", theme);
            await tick();
        };

        const startViewTransition = (document as ViewTransitionDocument).startViewTransition;
        if (startViewTransition) {
            const transition = startViewTransition.call(document, applyTheme);
            transition.finished.finally(() => {
                isThemeTransitioning = false;
                delete root.dataset.themeTransition;
            });
        } else {
            void applyTheme().then(() => {
                window.setTimeout(() => {
                    isThemeTransitioning = false;
                    delete root.dataset.themeTransition;
                }, 760);
            });
        }
    }

    function toggleLanguage() {
        language = language === "zh" ? "en" : "zh";
        document.documentElement.lang = language === "zh" ? "zh-CN" : "en";
        localStorage.setItem("notifybox-language", language);
    }
</script>

<svelte:head>
    <title>{t.appName}</title>
    <meta
        name="description"
        content="通知盒子：离线优先的 Android 通知收纳、统计与跨设备导出工具。" />
</svelte:head>

<div
    class="site-shell"
    class:zh={language === "zh"}
    id="top">
    <CursorRing />
    <Header
        appName={t.appName}
        {language}
        {theme}
        {isScrolled}
        onHome={() => pageScroll?.scrollToSection(0)}
        onLanguage={toggleLanguage}
        onTheme={toggleTheme} />
    <PageScroll
        bind:this={pageScroll}
        onActiveSectionChange={(index) => (activeSection = index)}
        onScrolledChange={(scrolled) => (isScrolled = scrolled)}>
        <HeroSection
            {t}
            {language}
            {theme}
            active={activeSection === 0} />
        <StatisticsSection
            {t}
            {language}
            {theme}
            active={activeSection === 1} />
        <AnalysisSection
            {t}
            {language}
            {theme}
            active={activeSection === 2} />
        <TransferSection
            {t}
            {language}
            {theme}
            active={activeSection === 3} />
        <Footer {t} />
    </PageScroll>
    <SectionRail
        {language}
        {activeSection}
        onNavigate={(index) => pageScroll?.scrollToSection(index)} />
</div>
