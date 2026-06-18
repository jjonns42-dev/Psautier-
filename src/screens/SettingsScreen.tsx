import React from 'react';
import { ScrollView, StatusBar, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Colors } from '../theme/colors';

export default function SettingsScreen() {
  return (
    <SafeAreaView style={styles.safe} edges={['top']}>
      <StatusBar barStyle="light-content" backgroundColor={Colors.darkMedium} />
      <View style={styles.header}>
        <Text style={styles.headerTitle}>À propos</Text>
        <Text style={styles.headerSubtitle}>Liturgie des Heures Byzantine</Text>
      </View>
      <ScrollView
        contentContainerStyle={styles.content}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Qu'est-ce que l'Horologion ?</Text>
          <Text style={styles.sectionText}>
            L'Horologion (Ὡρολόγιον) est le livre liturgique de l'Église byzantine qui contient
            les offices des Heures canoniques. Il sanctifie chaque heure de la journée par la
            prière psalmodique et les hymnes sacrés.
          </Text>
        </View>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Les huit offices</Text>
          {[
            { name: 'Mésonyktikon', time: 'Minuit (00h)', desc: 'Veille nocturne' },
            { name: 'Orthros', time: 'Aube (06h)', desc: 'Grand office du matin' },
            { name: 'Première Heure', time: 'Matin (07h)', desc: 'Lever du soleil' },
            { name: 'Troisième Heure', time: 'Matinée (09h)', desc: 'Pentecôte' },
            { name: 'Sixième Heure', time: 'Midi (12h)', desc: 'Crucifixion' },
            { name: 'Neuvième Heure', time: 'Après-midi (15h)', desc: 'Mort du Christ' },
            { name: 'Hespérinon', time: 'Soir (18h)', desc: 'Lumière du soir' },
            { name: 'Apodipnon', time: 'Nuit (21h)', desc: 'Avant le sommeil' },
          ].map((item) => (
            <View key={item.name} style={styles.officeItem}>
              <View style={styles.officeItemLeft}>
                <Text style={styles.officeName}>{item.name}</Text>
                <Text style={styles.officeDesc}>{item.desc}</Text>
              </View>
              <Text style={styles.officeTime}>{item.time}</Text>
            </View>
          ))}
        </View>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Tradition</Text>
          <Text style={styles.sectionText}>
            La pratique des Heures remonte aux origines du christianisme. Les moines des déserts
            d'Égypte et de Palestine ont structuré la journée en offices de prière, perpétuant
            la pratique juive de la prière aux heures fixes. L'Église byzantine a hérité et
            enrichi cette tradition, qui reste vivante dans les monastères du mont Athos et dans
            les paroisses du rite oriental.
          </Text>
        </View>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Kyrie eleison</Text>
          <View style={styles.quoteBox}>
            <Text style={styles.quoteGreek}>Κύριε ἐλέησον</Text>
            <Text style={styles.quoteTranslation}>Seigneur, aie pitié</Text>
            <Text style={styles.quoteNote}>
              Cette invocation, répétée tout au long des offices byzantins, est le cœur de la
              prière orientale. Elle exprime la totale dépendance de la créature envers son Créateur.
            </Text>
          </View>
        </View>

        <View style={styles.versionBox}>
          <Text style={styles.versionText}>Liturgie des Heures Byzantine</Text>
          <Text style={styles.versionNumber}>Version 1.0.0</Text>
          <Text style={styles.versionNote}>
            ✝  Ad maiorem Dei gloriam  ✝
          </Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: Colors.cream,
  },
  header: {
    backgroundColor: Colors.darkMedium,
    paddingHorizontal: 20,
    paddingVertical: 20,
  },
  headerTitle: {
    color: Colors.white,
    fontSize: 28,
    fontWeight: '800',
  },
  headerSubtitle: {
    color: Colors.goldLight,
    fontSize: 13,
    marginTop: 2,
    letterSpacing: 1,
  },
  content: {
    padding: 16,
    paddingBottom: 40,
  },
  section: {
    backgroundColor: Colors.white,
    borderRadius: 14,
    padding: 16,
    marginBottom: 14,
    borderWidth: 1,
    borderColor: Colors.border,
  },
  sectionTitle: {
    color: Colors.blue,
    fontSize: 16,
    fontWeight: '800',
    marginBottom: 10,
    letterSpacing: 0.3,
  },
  sectionText: {
    color: Colors.textPrimary,
    fontSize: 14,
    lineHeight: 22,
  },
  officeItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: Colors.creamDark,
  },
  officeItemLeft: {
    flex: 1,
  },
  officeName: {
    color: Colors.textPrimary,
    fontSize: 14,
    fontWeight: '700',
  },
  officeDesc: {
    color: Colors.textSecondary,
    fontSize: 12,
    marginTop: 1,
  },
  officeTime: {
    color: Colors.gold,
    fontSize: 12,
    fontWeight: '600',
  },
  quoteBox: {
    backgroundColor: Colors.cream,
    borderRadius: 10,
    padding: 14,
    alignItems: 'center',
  },
  quoteGreek: {
    color: Colors.burgundy,
    fontSize: 22,
    fontWeight: '700',
    fontStyle: 'italic',
    letterSpacing: 1,
  },
  quoteTranslation: {
    color: Colors.textSecondary,
    fontSize: 14,
    fontStyle: 'italic',
    marginTop: 4,
  },
  quoteNote: {
    color: Colors.textPrimary,
    fontSize: 13,
    lineHeight: 20,
    marginTop: 10,
    textAlign: 'center',
  },
  versionBox: {
    alignItems: 'center',
    padding: 20,
  },
  versionText: {
    color: Colors.textSecondary,
    fontSize: 13,
    fontWeight: '600',
  },
  versionNumber: {
    color: Colors.border,
    fontSize: 12,
    marginTop: 2,
  },
  versionNote: {
    color: Colors.gold,
    fontSize: 14,
    marginTop: 10,
    letterSpacing: 1,
  },
});
