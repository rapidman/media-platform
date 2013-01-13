<?xml version="1.0" encoding="utf-8" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <xsl:apply-templates select="rtmp"/>
        <xsl:value-of select="/rtmp/version"/>,
        <xsl:value-of select="/rtmp/pid"/>,
        <xsl:value-of select="/rtmp/built"/>&#160;<xsl:value-of select="/rtmp/compiler"/>
    </xsl:template>

    <xsl:template match="rtmp">
        <rtmp>
            <in>
                <xsl:value-of select="in"/>
            </in>
            <out>
                <xsl:value-of select="out"/>
            </out>
            <bwin>
                <xsl:value-of select="round(bwin div 1024)"/>
            </bwin>
            <bwout>
                <xsl:value-of select="round(bwout div 1024)"/>
            </bwout>
            <xsl:call-template name="showtime">
                <xsl:with-param name="time" select="/rtmp/uptime * 1000"/>
            </xsl:call-template>
            <xsl:apply-templates select="server"/>
        </rtmp>
    </xsl:template>

    <xsl:template match="server">
        <server>
            <xsl:apply-templates select="application"/>
        </server>
    </xsl:template>

    <xsl:template match="application">
        <application>
            <name>
                <xsl:value-of select="name"/>
            </name>
            <xsl:apply-templates select="live"/>
        </application>
    </xsl:template>

    <xsl:template match="live">
        <live>
            <nclients>
                <xsl:value-of select="nclients"/>
            </nclients>
            <xsl:apply-templates select="stream"/>
        </live>
    </xsl:template>

    <xsl:template match="stream">
        <stream>
            <name>
                <xsl:value-of select="name"/>
                <xsl:if test="string-length(name) = 0">
                    [EMPTY]
                </xsl:if>
            </name>
            <nclients>
                <xsl:value-of select="nclients"/>
            </nclients>
            <in>
                <xsl:value-of select="in"/>
            </in>
            <out>
                <xsl:value-of select="out"/>
            </out>
            <bwin>
                <xsl:value-of select="round(bwin div 1024)"/>
            </bwin>
            <bwout>
                <xsl:value-of select="round(bwout div 1024)"/>
            </bwout>
            <metaSize><xsl:value-of select="meta/width"/>x<xsl:value-of select="meta/height"/>
            </metaSize>
            <metaFramerate>
                <xsl:value-of select="meta/framerate"/>
            </metaFramerate>
            <metaVideo>
                <xsl:value-of select="meta/video"/>
                <xsl:apply-templates select="meta/profile"/>
                <xsl:apply-templates select="meta/level"/>
            </metaVideo>
            <metaAudio>
                <xsl:value-of select="meta/audio"/>
            </metaAudio>
            <xsl:call-template name="streamstate"/>
            <xsl:call-template name="showtime">
                <xsl:with-param name="time" select="time"/>
            </xsl:call-template>

            <xsl:attribute name="id">
                <xsl:value-of select="../../name"/>-<xsl:value-of select="name"/>
            </xsl:attribute>
            <xsl:apply-templates select="client"/>
        </stream>
    </xsl:template>

    <xsl:template name="showtime">
        <showtime>
            <xsl:param name="time"/>

            <xsl:variable name="sec">
                <xsl:value-of select="floor($time div 1000)"/>
            </xsl:variable>

            <xsl:if test="$sec &gt;= 86400">
                <xsl:value-of select="floor($sec div 86400)"/>d
            </xsl:if>

            <xsl:if test="$sec &gt;= 3600">
                <xsl:value-of select="(floor($sec div 3600)) mod 24"/>h
            </xsl:if>

            <xsl:if test="$sec &gt;= 60">
                <xsl:value-of select="(floor($sec div 60)) mod 60"/>m
            </xsl:if>

            <xsl:value-of select="$sec mod 60"/>s
        </showtime>
    </xsl:template>


    <xsl:template name="streamstate">
        <streamstate>
            <xsl:choose>
                <xsl:when test="active">active</xsl:when>
                <xsl:otherwise>idle</xsl:otherwise>
            </xsl:choose>
        </streamstate>
    </xsl:template>


    <xsl:template name="clientstate">
        <clientstate>
            <xsl:choose>
                <xsl:when test="publishing">publishing</xsl:when>
                <xsl:otherwise>playing</xsl:otherwise>
            </xsl:choose>
        </clientstate>
    </xsl:template>


    <xsl:template match="client">
        <client>
            <id>
                <xsl:value-of select="id"/>
            </id>
            <xsl:call-template name="clientstate"/>
            <address>
                <xsl:value-of select="address"/>
            </address>
            <flashver>
                <xsl:value-of select="flashver"/>
            </flashver>
            <href>
                <xsl:attribute name="href">
                    <xsl:value-of select="pageurl"/>
                </xsl:attribute>
                <xsl:value-of select="pageurl"/>
            </href>
            <swfurl>
                <xsl:value-of select="swfurl"/>
            </swfurl>
            <dropped>
                <xsl:value-of select="dropped"/>
            </dropped>
            <avsync>
                <xsl:value-of select="avsync"/>
            </avsync>
            <xsl:call-template name="showtime">
                <xsl:with-param name="time" select="time"/>
            </xsl:call-template>
        </client>
    </xsl:template>

    <xsl:template match="publishing">
        <publishing>
            publishing
        </publishing>
    </xsl:template>

    <xsl:template match="active">
        <active>
            active
        </active>
    </xsl:template>

    <xsl:template match="profile">
        <profile>
            /
            <xsl:value-of select="."/>
        </profile>
    </xsl:template>

    <xsl:template match="level">
        <level>
            /
            <xsl:value-of select="."/>
        </level>
    </xsl:template>

</xsl:stylesheet>
