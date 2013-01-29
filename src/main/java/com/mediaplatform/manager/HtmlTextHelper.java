package com.mediaplatform.manager;

import com.mediaplatform.util.ThreeTuple;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * хелповые функции, для работы с отображением постов (новости, дневник, коменты и проч)
 * Все методы возвращает html-escape текст
 *
 */
public class HtmlTextHelper {
    private static final int HEADER_TRUNCATION_LENGTH = 40; //величина усечения для заголовков страницы
    private static final int TITLE_TRUNCATION_LENGTH = 80; //величина усечения для заголовков
    private static final int TEXT_TRUNCATION_LENGTH = 160; //величина усечения для текста

	public static final String DEFAULT_ENDING="...";


	/* Открывающийся либо самостоятельный тег
	 * 1. тег может не иметь атрибутов
	 * 2. атрибутов мб 1 или более
	 * 3. атрибут может не иметь значения: <option selected />
	 * 4. значение атрибута мб в 3-х видах: <a val1=one val2='two' val3="three">
	 * 5. тег может быть закрыт сразу же или оставлен открытым
	 * Здесь мы ловим имя тега и блок с атрибутами
	 */
    private static final Pattern sTag=Pattern.compile("<([a-zA-Z0-9-]+)((?:\\s+[^\\s\"'>/=]+(?:\\s*=\\s*(?:[^\\s\"'=>]+?|'[^']*'|\"[^\"]*\"))?)+)?\\s*/?>", Pattern.DOTALL);

	/* ловим "опасные" атрибуты: события (имя атрибута начинается на on) и href*/
	private static final Pattern scriptAttrs=Pattern.compile("(?:\\bon[^\\s\"'>/=]+|href)\\s*=\\s*(?:[^\\s\"'=>]+?|'[^']*'|\"[^\"]*\")", Pattern.DOTALL);
	/* отбор атрибутов ОБЯЗАТЕЛЬНО дложен быть case insensitive !!! */
	private static final Pattern attrs=Pattern.compile("([^\\s\"'>/=]+)\\s*=\\s*([^\\s\"'=>]+?|'[^']*'|\"[^\"]*\")", Pattern.DOTALL);

	/* Закрывающийся тег. Ловим тока имя тега */
	private static final Pattern eTag=Pattern.compile("</([a-zA-Z0-9-]+)\\s*>", Pattern.DOTALL);
	public static final String BR_REGEX="<br\\s*/?>";
	private static final Pattern brTag=Pattern.compile(BR_REGEX);

    private static final Pattern objectTag = Pattern.compile("(<object[^>]*>.*?</object[^>]*>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern embedTag = Pattern.compile("(<embed[^>]*>.*?</embed[^>]*>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final String VIDEO_WAP_REPLACEMENT = "<img src=\"/i/video-ico-small.gif\" alt=\"video\"/>";

	/* "пробелы" в начале и конце для хтмл строк*/
	private static final Pattern htmlTrimStart=Pattern.compile("^(?:"+BR_REGEX+"|\\s)+");
	private static final Pattern htmlTrimEnd=Pattern.compile("(?:"+BR_REGEX+"|\\s)+$");
	private static final Pattern startsWithBr = Pattern.compile("^\\s*" + BR_REGEX);


	/* сепаратор для отделения зоны превью. */
	public static final String BLOG_TEXT_PREVIEW_SEPARATOR="[cut]";
    /* bb коды [code][/code] & [quote][/quote] */
    public static final String BLOG_TEXT_CODE_START = "[code]";
    public static final String BLOG_TEXT_CODE_START_HTML = "<span class=\"bbcode-code\">";
    public static final String BLOG_TEXT_CODE_STOP = "[/code]";
    public static final String BLOG_TEXT_CODE_STOP_HTML = "</span>";
    public static final String BLOG_TEXT_QUOTE_START = "[quote]";
    public static final String BLOG_TEXT_QUOTE_START_HTML = "<span class=\"bbcode-quote\">";
    public static final String BLOG_TEXT_QUOTE_STOP = "[/quote]";
    public static final String BLOG_TEXT_QUOTE_STOP_HTML = "</span>";


//классичесие смайлы, которые описываются символами
    private static final String[][] simpleSmiles=new String[][]{
            {"^_^", "happy.gif"}, {";\\)", "wink.gif"}, {":P", "tongue.gif"}, {":-D", "biggrin2.gif"}, {"B\\)", "cool.gif"},
            {"-_-", "sleep.gif"}, {"<_<", "dry.gif"}, {":\\)", "smile.gif"}, {":\\(", "sad.gif"}, {":D", "biggrin.gif"},
            {":o", "ohmy.gif"}
    };

    //смайлы, которые описываются в виде последовательности  - :smile-code:, например :lol:
    private static final String[][] customSmiles=new String[][]{
            {"mellow", "mellow.gif"},        {"huh", "huh.gif"},         {"lol", "laugh.gif"},      {"rolleyes", "rolleyes.gif"}, {"wub", "wub.gif"},
            {"angry", "mad.gif"},            {"unsure", "unsure.gif"},   {"wacko", "wacko.gif"},    {"blink", "blink.gif"},       {"argue", "argue.gif"},
            {"clap", "clap.gif"},            {"loly", "lol.gif"},        {"no", "no.gif"},          {"nono", "nono.gif"},         {"thumbdown", "thumbdown.gif"},
            {"thumbsup", "thumbsup.gif"},    {"worthy", "worthy.gif"},   {"yes", "yes.gif"},        {"idea", "Lighten.gif"},      {"punk", "punk.gif"},
            {"beer", "drinks_cheers.gif"},   {"diespam", "diespam.gif"}, {"box", "boxing.gif"},     {"cry", "cry.gif"},           {"shutup", "shutup.gif"},
            {"whistle", "whistling.gif"},    {"yuck", "yucky.gif"},      {"kiss", "wink_kiss.gif"}, {"whip", "whip.gif"},         {"blow", "blow.gif"},
            {"angel", "angel_innocent.gif"}, {"bash", "bash.gif"},       {"bb", "bb.gif"},          {"ibeer", "beer.gif"},        {"bounce", "bounce.gif"},
            {"brows", "brows.gif"},          {"clap2", "clap_1.gif"},    {"evil", "evil_3.gif"},    {"furious", "furious.gif"},   {"greedy", "greedy.gif"},
            {"gun", "gun_bandana.gif"},      {"inv", "inv.gif"},         {"lac", "lac.gif"},        {"offtop", "poster_offtopic.gif"}, {"ranting", "ranting_w.gif"},
            {"sleep", "sleep_1.gif"},        {"up", "thumbup2.gif"},     {"wallbash", "wallbash.gif"}, {"megalol", "rofl.gif"},   {"blush", "blushing.gif"},
            {"crazy", "crazy.gif"},          {"mobile", "mobile.gif"},   {"baby", "baby.gif"},      {"wink", "bigwink2.gif"},     {"banned", "banned.gif"},
            {"king", "king.gif"},            {"bleh", "bleh.gif"},       {"blush2", "blush2.gif"},  {"book", "bestbook.gif"},     {"devil", "devil_2.gif"},
            {"cens", "censoree.gif"},        {"shuffle", "shuffle.gif"}, {"eat", "essen.gif"},      {"gossip", "gossip.gif"},     {"harhar", "harhar.gif"},
            {"console", "console.gif"},      {"para", "para.gif"},       {"slap", "slap.gif"},      {"drunk", "drinks.gif"},      {"hi", "hi.gif"},
            {"bye", "bye.gif"},              {"balloon", "balloon.gif"}, {"flower", "flowers1.gif"},{"sex", "sex.gif"},           {"bigwink", "bigwink.gif"},
            {"=", "=.gif"},                  {"09", "09.gif"},           {"1", "1.gif"},            {"1321", "1321.gif"},         {"1ewytryw", "1ewytryw.gif"},
            {"2", "2.gif"},                  {"4", "4.gif"},             {"6957", "6957.gif"},      {"7", "7.gif"},               {"76975", "76975.gif"},
            {"8", "8.gif"},                  {"ads", "ads.gif"},         {"ads2", "ads2.gif"},      {"asdf", "asdf.gif"},         {"dfh", "dfh.gif"},
            {"dh", "dh.gif"},                {"dsfg", "dsfg.gif"},       {"fegh", "fegh.gif"},      {"hgk", "hgk.gif"},           {"qfwe", "qfwe.gif"},
            {"scbv", "scbv.gif"},            {"SRADF", "SRADF.gif"},     {"xcb", "xcb.gif"},        {"xvaz", "xvaz.gif"}
    };

    //смайлы, которые не попадают ни в одну из категорий (следствие интеграции с форумами космо)
    private static final String[][] miscSmiles=new String[][]{
            {"uaah:", "CA9YRV1N.gif"},  {"chupik2", "sla.gif"}
    };

    private static final Pattern customSmilePattern = Pattern.compile(":([a-zA-Z0-9=]+):");


    /**
     * производит форматирование текста:
     * - html escape
     * - замена переводов строк на <br/>
     * @param str
     * @return html-escape текст
     */
    public static String format(String str) {
        //return HtmlUtils.htmlEscape(str).replace(System.getProperty("line.separator"), "<br />");
        return formatNoEscape(HtmlUtils.htmlEscape(str));
    }

    public static String formatNoEscape(String str) {
        return str.replace(System.getProperty("line.separator"), "<br />");
    }
	
	/**
	 * подрезает текст без сохранения форматирования (переводы строк и т.п.)
	 */
	//!!! наверно не стоит высталять наружу в таком виде, т.к. в жсп может быть исопльзован для разных строк (в т.ч где надо сначала поудалять все теги и т.п.)
	public static String abbreviate(String str, int truncLen, String ending) {
		return abbreviate(unescape(str), truncLen, ending, false);
	}

	/**
	 * подрезает текст до заданной длины, обрабатывает результат согласно параметру format и к концу прибавляет ending, если подрезка имела место
	 * @param format если true, то производит форматирование текста, иначе только html-escape
	 */
	private static String abbreviate(String str, int truncLen, String ending, boolean format) {
		String txt= StringUtils.trimToEmpty(str);
		boolean truncable=(!StringUtils.isEmpty(txt) && txt.length() > truncLen);
		//подрезаем
		if (truncable) {
			txt=txt.substring(0, truncLen);
		}
		//форматируем
		if (format) txt = format(txt);
		else {
			txt = HtmlUtils.htmlEscape(txt);
		}

		//добавляем конец
		if (truncable) {
			txt = txt + ending;
		}
		return txt;
	}

	/**
	 *
	 * @param header текст заголовка. Считается, что заголовок передается проэскейпленным (содержит эскейп последовательности)	 
	 * @return html-escape текст
	 */
    public static String truncateHeader(String header){
        return abbreviate(unescape(header), HEADER_TRUNCATION_LENGTH, DEFAULT_ENDING, false);
    }

	/**
	 * подрезает заголовок,  если он велик + производит html escape
	 * @param title заголвок для подрезки. Считается, что заголовок передается проэскейпленным (содержит эскейп последовательности)
	 * @return html-escape текст
	 */
	public static String truncateTitle(String title, String ending) {
		return abbreviate(unescape(title), TITLE_TRUNCATION_LENGTH, ending, false);
	}

	/**
	 * то же, что и {@link HtmlTextHelper#truncateTitle(String, String)},
	 * но использует дефолтное окнчание
	 * @return html-escape текст
	 */
	public static String truncateTitle(String title) {
		return truncateTitle(title, DEFAULT_ENDING);
	}

	/**
	 * то же, что и {@link HtmlTextHelper#truncateTitle(String, String)},
	 * но использует дефолтное окнчание с урл
	 */
	public static String truncateTitleWithUrl(String title, String url) {
		return truncateTitle(title, "<a href=\"" + url + "\">" + DEFAULT_ENDING + "</a>");
	}

	/**
	 * подрезает тело поста, если он велико + производит его форматирование
	 * @param text текст для подрезки. Считается, что переданный текст содержить эскейп-последовательности (может содержать) 
	 * @return html-escape текст
	 */
	public static String truncateText(String text, String ending) {
		return abbreviate(unescape(text), TEXT_TRUNCATION_LENGTH, ending, true);
	}

	/**
	 * то же, что и {@link HtmlTextHelper#truncateText(String, String)},
	 * но использует дефолтное окнчание
	 */
	public static String truncateText(String text) {
		return truncateText(text, DEFAULT_ENDING);
	}

	/**
	 * то же, что и {@link HtmlTextHelper#truncateText(String)},
	 * но использует дефолтное окнчание с урл
	 */
	public static String truncateTextWithUrl(String text, String url) {
		return truncateText(text, "<a href=\"" + url + "\">" + DEFAULT_ENDING + "</a>");
	}



    //TODO: подумать как выглядит обрезание текста, если в нем есть \n? как бороться?




	public static String htmlToPlain(String src) {
		return htmlToPlain(src, true);
	}

	/**
	 * Удаляет все теги, оставляя только текст. &lt;br/&gt; заменяется на символ перевода строки
	 * @param src
	 * @param preserveLineBreaks если тру, то переводы строк будут сохранены.
	 * @return
	 */
	public static String htmlToPlain(String src, boolean preserveLineBreaks) {
		src = removeHtmlTags(src, preserveLineBreaks);
       
		//заменяем эскеп-последовательности нормальными символами
		src = HtmlUtils.htmlUnescape(src);

		return src;
	}

	/**
	 * удаляет все html-теги. Тег br заменяется на перевод строки
	 * @param src
	 * @return
	 */
	private static String removeHtmlTags(String src, boolean preserveLineBreaks) {
		//заменяем <br> на перевод строк
		Matcher brm=brTag.matcher(src);
		src=brm.replaceAll(System.getProperty("line.separator"));

		if (!preserveLineBreaks) {
			//убираем все переводы строк, заменяя группу переводов на один пробел
			src = src.replaceAll("[\n]+", " ");
		}

		//убиваем теги (убираем по отдельности, т.к. в хтмл теги могут быть не закрытыми, с нарушенной вложенностью и т.п., поэтому по раздельности все проще чистить)
		Matcher m=sTag.matcher(src);
		src=m.replaceAll("");
		m=eTag.matcher(src);
		src=m.replaceAll("");

		return src;
	}

	/**
	 * удаляет "опасные атрибуты", либо опасные значения атрибутов (касается всяких штук типа <a href="javascript:alert('Donate!')">)
	 * @param src строка с атрибутами
	 */
	private static void appendScriptSafeAttributes(String src, StringBuffer sb) {
		src = src.toLowerCase();
		Matcher m = scriptAttrs.matcher(src);
		String av;
		int pos = 0;
		while (m.find()) {
			sb.append(src.substring(pos, m.start()));
			pos = m.end();

			av = m.group();
			//всякие там onclick и проч. игнорим без разбора. Для href надо посмотреть значение
			if (av.startsWith("href")) {
				//sb.append(" ");
				if (av.contains("javascript:") || av.contains("livescript:")) {
					sb.append("href=\"#\"");
				}
				else {
					sb.append(av);
				}
			}
		}
		//добавим остаток
		sb.append(src.substring(pos));
	}
    
	public static StringBuffer processTags(StringBuffer src, boolean checkCloseTags, TagCallback tc) {
		StringBuffer sb=new StringBuffer(src.length());

		Matcher m=sTag.matcher(src);
		int pos=0;
		while(m.find()){
			//накапливаем результат
			sb.append(src.substring(pos, m.start()));

			pos=tc.onTag(m.group(1), m.group(2), m.start(), m.end(), m.end(1), m.end(2), sb);
			if(pos<m.start()) pos=m.end();
		}
		//добавим остаток
		sb.append(src.substring(pos));

        //теперь аналогично пройдемся по закрывающим тегам
        if (checkCloseTags) {
            StringBuffer sb1 = new StringBuffer(sb.length());
            m = eTag.matcher(sb);
            pos = 0;
            while (m.find()) {
                String tagName = m.group(1).toLowerCase();
                //накапливаем результат
                sb1.append(sb.substring(pos, m.start()));

                pos = tc.onEndTag(tagName, m.start(), m.end(), sb1);
                if(pos<m.start()) pos=m.end();
            }
            //добавим остаток
            sb1.append(sb.substring(pos));

            return sb1;
        }

        return sb;
	}

    public static abstract class TagCallback{
		public abstract int onTag(String tagName, String attrs, int start, int end, int nameEnd, int attrsEnd, StringBuffer sb);
        public int onEndTag(String tagName, int start, int end, StringBuffer sb){
            return start; //по дефлоту не фильтруем ничего
        }
	}

	/**
	 * ищет заданный атрибут строке атрибутов.
	 * Если находит, то вертает его знаение, и начальный и конечную позиции всего блока атрибута.
	 * Если не находит, то возвращает null     
	 */
	public static ThreeTuple<String, Integer, Integer> findAttribute(String name, String src) {

		Matcher m = attrs.matcher(src);
		while (m.find()) {
			String aN = m.group(1);
			if (aN.equalsIgnoreCase(name)) {
				String val = m.group(2);
				if (val.startsWith("\"") || val.startsWith("'")) {
					val = val.substring(1, val.length() - 1);
				}
				return new ThreeTuple(val, m.start(), m.end());
			}
		}
		return null;
	}


	/**
	 * подрезает текст по дефолтному сепаратору
	 * @return начало текста до сепаратора, либо весь текст, если сепаратор не найден
	 */
	public static String cut(String src) {
		return StringUtils.substringBefore(src, BLOG_TEXT_PREVIEW_SEPARATOR);
	}

	/**
	 * убирает из текста спец теги, которые не предназначены для вывода пользователю, а служат лишь как пометки для служебных целей.
	 * @return
	 */
	public static String hideSpecialTags(String src) {
		src= StringUtils.replace(src, BLOG_TEXT_PREVIEW_SEPARATOR, "");
        src = replaceBBCodes(src, true);
		return src;
	}

    /**
     * заменяет BB-теги в тексте на их html эквиваленты, либо удаляет их из текста, оставляя тольлько содержмое между bb-тегами
     * [cut] хоть и является BB-кдодм, но здесь он не обрабаывается, т.к. не имеет хтмл эквивалента и выодлняет служебную функцию 
     * @param src
     * @param clean если тру, то теги будут удалены (аналог htmlToPlain)
     */
    public static String replaceBBCodes(String src, boolean clean) {
        src= StringUtils.replace(src, BLOG_TEXT_CODE_START, clean ? "" : BLOG_TEXT_CODE_START_HTML);
        src= StringUtils.replace(src, BLOG_TEXT_CODE_STOP, clean ? "" : BLOG_TEXT_CODE_STOP_HTML);
        src= StringUtils.replace(src, BLOG_TEXT_QUOTE_START, clean ? "" : BLOG_TEXT_QUOTE_START_HTML);
        src= StringUtils.replace(src, BLOG_TEXT_QUOTE_STOP, clean ? "" : BLOG_TEXT_QUOTE_STOP_HTML);
        return src;
    }

	/**
	 * функция возвращает потенциальный заголовок. Если title не пустой, то будет возвращем он.
	 * Иначе в качестве заголовка будет использовано начало текста.
	 * @param title фактический заголвок
	 * @param text фактическое тело, считается, что текст представлен в виде валидного хтмл (с эскейп-последовательностями, возможными тэгами и т.п.)	  
	 * @param truncateTitle если тру, то результат будет подрезан вне зависимости от того, что было взято в качестве заголовка.
	 * @return всегда escaped текст
	 */
	public static String suggestTitle(String title, String text, boolean truncateTitle) {
		if (!StringUtils.isEmpty(title)) {
//			return HtmlUtils.htmlEscape(truncateTitle ? StringUtils.abbreviate(title, TITLE_TRUNCATION_LENGTH) : title);
			//все заголовки автоматом эскейпятся при сохранении, поэтому если надо его подрезать, то сначала убираем все эскейпы
			if (truncateTitle) {
				return HtmlUtils.htmlEscape(StringUtils.abbreviate(unescape(title), TITLE_TRUNCATION_LENGTH));
			}
			return title;
		}

		//если текст в виде хтмл, т надо сначала сбросить все форматирование
		String t=hideSpecialTags(text);
		t = htmlToPlain(t, false);

		return HtmlUtils.htmlEscape(StringUtils.abbreviate(t, TITLE_TRUNCATION_LENGTH));

	}

    /**
     * возвращает видимую часть текста (то, что юзер увидит в браузере как текст)
     * Т.е. удаляет все спец теги, bb-коды (т.е. они будут преобразованы в хтмл и не будут являтся видимым контентом)
     * и удаляет хмтл теги, если сказано, что текст считать хтмлом.
     * @param text
     * @param treatAsHtml
     * @return
     */
    public static String visibleText(String text, boolean treatAsHtml) {
        String s = HtmlTextHelper.hideSpecialTags(text);
		if (treatAsHtml) {
			return HtmlTextHelper.htmlToPlain(s);
		}
        return s;
    }

	/**
	 * удаляет из хтмл строки ведущие и конечные переводы строк и пробелы
	 * @return
	 */
	public static String trimHtml(String src) {
		Matcher m = htmlTrimStart.matcher(src);
		src=m.replaceAll("");
		m=htmlTrimEnd.matcher(src);
		return m.replaceAll("");
	}


    public static String showSmiles(String text) {
        for (String[] sa : simpleSmiles) {
            String code = sa[0];
            String img = sa[1];
            text = text.replaceAll(sa[0], "<img emoid=\"" + code + "\" border=\"0\" alt=\"" + img + "\" src=\"/js/wysiwyg/themes/advanced/img/smiles/" + img + "\"/>");
        }
        for (String[] sa : miscSmiles) {
            String code = sa[0];
            String img = sa[1];
            text = text.replaceAll(sa[0], "<img emoid=\"" + code + "\" border=\"0\" alt=\"" + img + "\" src=\"/js/wysiwyg/themes/advanced/img/smiles/" + img + "\"/>");
        }

        //кастомые смайлилки меняет по регекспу
        StringBuffer sb=new StringBuffer();
        Matcher m = customSmilePattern.matcher(text);
        while (m.find()) {
            String code = m.group(1);
            for (String[] sa : customSmiles) {
                if (code.equals(sa[0])) {
                    String img = sa[1];
                    m.appendReplacement(sb, "<img emoid=\"" + m.group() + "\" border=\"0\" alt=\"" + img + "\" src=\"/js/wysiwyg/themes/advanced/img/smiles/" + img + "\"/>");
                }
            }

        }
        m.appendTail(sb);

        return sb.toString();
    }

    public static String escape(String str, int length){
        if (str == null) return "";
        str = HtmlUtils.htmlEscape(str); 
        if (length > 3){
            str = StringUtils.abbreviate(str, length);
        }
        return str;
    }

    public static String unescape(String str){
        if (str == null) return "";
        return HtmlUtils.htmlUnescape(str);
    }


    /**
     * фильтрует имя профайла/акаунта таким образом, чтобы емайлы и номера телефонов не были видны
     * Правила (из ТЗ)
     * 1) Если обнаружен символ “@” - заменять часть логина включая @ и до 4 символов после на “…”
     * 2) Если обнаружено “.ru”, “.com” - заменять до 4 символов до вхождения на “…”
     * 3) Если в логине обнаружено 7 цифр и более (не обязательно подряд) - заменяем символы начиная с предпоследней с конца цифры на “…”.
     * На данный момент фильтрование отображения ников - чисто диснеевская фича. С одной стороны у нас не было контроля "неправильных ников"
     * на момент регистрации. С другой стороны, этого нет и в IUR. Поэтому даже если у нас в регистрации будет проверка, то тех, кто сразу пришел из
     * IUR (был зареген на к-л других диснеевских сайтах), все равно надо фильтровать. 
     *
     */
    private static final Pattern safeNameRule1 = Pattern.compile("(@.{1,4})");
    private static final Pattern safeNameRule2 =Pattern.compile("[^.]{3,4}(\\.com|\\.ru)"); /*замену производим только,
     если 4 символа перед .ru и .com не содержат хотя бы 1-й точки. Т.е. варианты типа ".zz..ru", "...z.ru", ".com.ru"
      и тп. заменять не будем (иначе от ника совсем ничего может не остаться)*/
    public static final Pattern safeNameRule3 =Pattern.compile("\\d");
    
    public static String safeName(String s) {        
        s= safeNameRule1.matcher(s).replaceAll("...");
        s = safeNameRule2.matcher(s).replaceAll("...$1");

        Matcher m = safeNameRule3.matcher(s);
        int i=0; //кол-во цифт в строке (не обязательно по порядку)        
        int ls = -1; //позиция последней цифры
        int pls = -1; //позиция предпоследней цифры
        while (m.find()) { //считаем кол-во цифр, одновременно определяя позицию предпоследней цифры
            i++;
            if (i>1) {
                pls = ls;
            }
            ls = m.start();
        }
        if (i>6) { //если кол-во цифр 7 и более, то "обрубаем хвост"
            s = s.substring(0, pls).concat("...");
        }
        
        return s;
    }

}