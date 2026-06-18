import React, { useState } from 'react';
import {
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import type { OfficeHour } from '../data/offices';
import { Colors } from '../theme/colors';

interface OfficeScreenProps {
  route: any;
  navigation: any;
}

type Tab = 'office' | 'psaumes' | 'lectures';

export default function OfficeScreen({ route, navigation }: OfficeScreenProps) {
  const office = route.params.office as OfficeHour;
  const [activeTab, setActiveTab] = useState<Tab>('office');

  return (
    <SafeAreaView style={styles.safe} edges={['top']}>
      <StatusBar barStyle="light-content" backgroundColor={Colors.burgundy} />

      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backBtn}>
          <Text style={styles.backText}>← Retour</Text>
        </TouchableOpacity>
        <Text style={styles.headerGreek}>{office.nameGreek}</Text>
        <Text style={styles.headerName}>{office.name}</Text>
        <Text style={styles.headerFr}>{office.nameFr}</Text>
      </View>

      {/* Tabs */}
      <View style={styles.tabs}>
        {(['office', 'psaumes', 'lectures'] as Tab[]).map((tab) => (
          <TouchableOpacity
            key={tab}
            style={[styles.tab, activeTab === tab && styles.tabActive]}
            onPress={() => setActiveTab(tab)}
          >
            <Text style={[styles.tabText, activeTab === tab && styles.tabTextActive]}>
              {tab === 'office' ? 'Office' : tab === 'psaumes' ? 'Psaumes' : 'Lectures'}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        {activeTab === 'office' && (
          <View>
            {/* Description */}
            <View style={styles.descriptionBox}>
              <Text style={styles.descriptionText}>{office.description}</Text>
            </View>

            {/* Tropaire */}
            <View style={styles.section}>
              <Text style={styles.sectionTitle}>Tropaire</Text>
              <View style={styles.tropaire}>
                <Text style={styles.tropairText}>{office.tropaire}</Text>
              </View>
            </View>

            {/* Prière de fin */}
            <View style={styles.section}>
              <Text style={styles.sectionTitle}>Prière de clôture</Text>
              <View style={styles.priereFin}>
                <Text style={styles.priereFinText}>{office.priereFin}</Text>
              </View>
            </View>

            {/* Heure */}
            <View style={styles.hourBadge}>
              <Text style={styles.hourBadgeText}>
                ✝  Heure canonique : {String(office.hour).padStart(2, '0')}h00
              </Text>
            </View>
          </View>
        )}

        {activeTab === 'psaumes' && (
          <View>
            {office.psaumes.map((psaume, idx) => (
              <View key={idx} style={styles.psaumeBlock}>
                <View style={styles.psaumeHeader}>
                  <Text style={styles.psaumeNumero}>Psaume {psaume.numero}</Text>
                  <Text style={styles.psaumeTitre}>{psaume.titre}</Text>
                </View>
                <Text style={styles.psaumeTexte}>{psaume.texte}</Text>
              </View>
            ))}
          </View>
        )}

        {activeTab === 'lectures' && (
          <View>
            {office.lectures.map((lecture, idx) => (
              <View key={idx} style={styles.lectureBlock}>
                <View style={styles.lectureRef}>
                  <Text style={styles.lectureRefText}>{lecture.reference}</Text>
                </View>
                <Text style={styles.lectureTexte}>{lecture.texte}</Text>
              </View>
            ))}
          </View>
        )}
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
    backgroundColor: Colors.burgundy,
    paddingHorizontal: 20,
    paddingTop: 8,
    paddingBottom: 24,
  },
  backBtn: {
    marginBottom: 12,
  },
  backText: {
    color: 'rgba(255,255,255,0.8)',
    fontSize: 14,
    fontWeight: '600',
  },
  headerGreek: {
    color: Colors.goldLight,
    fontSize: 13,
    fontStyle: 'italic',
    letterSpacing: 1,
    marginBottom: 4,
  },
  headerName: {
    color: Colors.white,
    fontSize: 28,
    fontWeight: '800',
    letterSpacing: 0.5,
  },
  headerFr: {
    color: 'rgba(255,255,255,0.75)',
    fontSize: 14,
    marginTop: 4,
  },
  tabs: {
    flexDirection: 'row',
    backgroundColor: Colors.white,
    borderBottomWidth: 1,
    borderBottomColor: Colors.border,
  },
  tab: {
    flex: 1,
    paddingVertical: 12,
    alignItems: 'center',
    borderBottomWidth: 3,
    borderBottomColor: 'transparent',
  },
  tabActive: {
    borderBottomColor: Colors.burgundy,
  },
  tabText: {
    fontSize: 13,
    fontWeight: '600',
    color: Colors.textSecondary,
  },
  tabTextActive: {
    color: Colors.burgundy,
  },
  scroll: {
    flex: 1,
  },
  scrollContent: {
    padding: 16,
    paddingBottom: 40,
  },
  descriptionBox: {
    backgroundColor: Colors.blue,
    borderRadius: 14,
    padding: 18,
    marginBottom: 16,
  },
  descriptionText: {
    color: Colors.white,
    fontSize: 15,
    lineHeight: 24,
    fontStyle: 'italic',
  },
  section: {
    marginBottom: 16,
  },
  sectionTitle: {
    color: Colors.textSecondary,
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 1.5,
    textTransform: 'uppercase',
    marginBottom: 8,
  },
  tropaire: {
    backgroundColor: Colors.white,
    borderRadius: 12,
    padding: 16,
    borderLeftWidth: 4,
    borderLeftColor: Colors.gold,
  },
  tropairText: {
    color: Colors.textPrimary,
    fontSize: 15,
    lineHeight: 24,
    fontStyle: 'italic',
  },
  priereFin: {
    backgroundColor: Colors.white,
    borderRadius: 12,
    padding: 16,
    borderLeftWidth: 4,
    borderLeftColor: Colors.burgundy,
  },
  priereFinText: {
    color: Colors.textPrimary,
    fontSize: 15,
    lineHeight: 24,
    fontStyle: 'italic',
  },
  hourBadge: {
    backgroundColor: Colors.creamDark,
    borderRadius: 10,
    padding: 12,
    alignItems: 'center',
    marginTop: 8,
  },
  hourBadgeText: {
    color: Colors.textSecondary,
    fontSize: 13,
    fontWeight: '600',
  },
  psaumeBlock: {
    backgroundColor: Colors.white,
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: Colors.border,
  },
  psaumeHeader: {
    marginBottom: 10,
    borderBottomWidth: 1,
    borderBottomColor: Colors.creamDark,
    paddingBottom: 8,
  },
  psaumeNumero: {
    color: Colors.gold,
    fontSize: 12,
    fontWeight: '700',
    letterSpacing: 1,
    textTransform: 'uppercase',
  },
  psaumeTitre: {
    color: Colors.textPrimary,
    fontSize: 18,
    fontWeight: '700',
    marginTop: 2,
  },
  psaumeTexte: {
    color: Colors.textPrimary,
    fontSize: 15,
    lineHeight: 26,
    fontStyle: 'italic',
  },
  lectureBlock: {
    backgroundColor: Colors.white,
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: Colors.border,
  },
  lectureRef: {
    backgroundColor: Colors.blue,
    borderRadius: 8,
    paddingHorizontal: 10,
    paddingVertical: 5,
    alignSelf: 'flex-start',
    marginBottom: 12,
  },
  lectureRefText: {
    color: Colors.white,
    fontSize: 12,
    fontWeight: '700',
  },
  lectureTexte: {
    color: Colors.textPrimary,
    fontSize: 15,
    lineHeight: 26,
    fontStyle: 'italic',
  },
});
